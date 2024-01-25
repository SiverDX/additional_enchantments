package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import de.cadentem.additional_enchantments.capability.Configuration;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.config.ClientConfig;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.OreSightEnchantment;
import de.cadentem.additional_enchantments.mixin.LevelRendererAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler {
    private static final List<LineData> RARE_LINES = new ArrayList<>();
    private static final List<LineData> UNCOMMON_LINES = new ArrayList<>();
    private static final List<LineData> COMMON_LINES = new ArrayList<>();
    private static final List<LineData> MISC_LINES = new ArrayList<>();

    private static final Map<Integer, SectionData> SECTION_CACHE = new WeakHashMap<>();
    private static final Map<Long, BlockData> BLOCK_CACHE = new WeakHashMap<>();

    private record LineData(Vector3f from, Vector3f to, Vector3f normal) {
    }

    private record BlockData(OreSightEnchantment.OreRarity rarity, long tickCount) {
    }

    private record SectionData(boolean containsOres, long tickCount) {
    }

    @SubscribeEvent
    public static void clearCache(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player && player == ClientProxy.getLocalPlayer()) {
            SECTION_CACHE.clear();
            BLOCK_CACHE.clear();
        }
    }

    @SubscribeEvent
    public static void handleOreSightEnchantment(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            int enchantmentLevel = OreSightEnchantment.getClientEnchantmentLevel();

            if (enchantmentLevel > 0) {
                ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                    PoseStack poseStack = event.getPoseStack();
                    poseStack.pushPose();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.disableDepthTest();

                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

                    collectAndDrawLines(localPlayer, enchantmentLevel + 1, configuration, poseStack, bufferBuilder, event.getLevelRenderer());

                    tesselator.end();
                    poseStack.popPose();
                    RenderSystem.enableDepthTest();
                    RenderType.cutout().clearRenderState();
                });
            }
        }
    }

    private static void drawGroupedLines(final PoseStack poseStack, final BufferBuilder bufferBuilder) {
        for (int i = OreSightEnchantment.OreRarity.values().length - 1; i >= 0; i--) {
            OreSightEnchantment.OreRarity rarity = OreSightEnchantment.OreRarity.values()[i];

            if (rarity == OreSightEnchantment.OreRarity.NONE) {
                continue;
            }

            Vec3i color = switch (rarity) {
                case ALL -> new Vec3i(255, 255, 255);
                case COMMON -> new Vec3i(165, 42, 42);
                case UNCOMMON -> new Vec3i(255, 215, 0);
                case RARE -> new Vec3i(64, 224, 208);
                default -> throw new IllegalStateException("Unexpected value: " + rarity);
            };

            List<LineData> rarityLines = getLines(rarity);

            for (LineData line : rarityLines) {
                bufferBuilder.vertex(poseStack.last().pose(), line.from.x(), line.from.y(), line.from.z()).color(color.getX(), color.getY(), color.getZ(), 255).normal(line.normal.x(), line.normal.y(), line.normal.z()).endVertex();
                bufferBuilder.vertex(poseStack.last().pose(), line.to.x(), line.to.y(), line.to.z()).color(color.getX(), color.getY(), color.getZ(), 255).normal(line.normal.x(), line.normal.y(), line.normal.z()).endVertex();
            }

            rarityLines.clear();
        }
    }

    /**
     * Referenced off of <a href="https://github.com/TelepathicGrunt/Bumblezone/blob/31cc8dc7066fc19dd0b880f66d64460cee4d356b/common/src/main/java/com/telepathicgrunt/the_bumblezone/items/essence/LifeEssence.java#L132">TelepathicGrunt</a>
     */
    private static void collectAndDrawLines(final LocalPlayer localPlayer, int radius, final Configuration configuration, final PoseStack poseStack, final BufferBuilder bufferBuilder, final LevelRenderer levelRenderer) {
        if (configuration.oreRarity == OreSightEnchantment.OreRarity.NONE) {
            return;
        }

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos startPosition = localPlayer.blockPosition();
        ChunkPos currentChunkPosition = new ChunkPos(startPosition);
        LevelChunk currentChunk = null;

        int minChunkX = startPosition.getX() - radius;
        int maxChunkX = startPosition.getX() + radius;
        int minChunkY = Math.max(localPlayer.getLevel().getMinBuildHeight(), startPosition.getY() - radius);
        int maxChunkY = Math.min(localPlayer.getLevel().getMaxBuildHeight(), startPosition.getY() + radius);
        int minChunkZ = startPosition.getZ() - radius;
        int maxChunkZ = startPosition.getZ() + radius;

        boolean foundSection = false;

        BlockPos.MutableBlockPos mutablePosition = BlockPos.ZERO.mutable();

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                int sectionX = SectionPos.blockToSectionCoord(x);
                int sectionZ = SectionPos.blockToSectionCoord(z);

                if (currentChunk == null || currentChunkPosition.x != sectionX || currentChunkPosition.z != sectionZ) {
                    currentChunkPosition = new ChunkPos(sectionX, sectionZ);
                    currentChunk = localPlayer.getLevel().getChunk(sectionX, sectionZ);
                }

                foundSection = false;

                for (int y = maxChunkY; y >= minChunkY; y--) {
                    int sectionIndex = currentChunk.getSectionIndex(y);
                    LevelChunkSection section = currentChunk.getSection(sectionIndex);

                    mutablePosition.set(x, y, z);
                    BlockData blockData = BLOCK_CACHE.get(mutablePosition.asLong());

                    OreSightEnchantment.OreRarity rarity = null;

                    if (blockData != null && localPlayer.tickCount - blockData.tickCount > 20L * ClientConfig.CACHE_KEPT_SECONDS.get()) {
                        BLOCK_CACHE.remove(mutablePosition.asLong());
                        blockData = null;
                    } else if (blockData != null) {
                        rarity = blockData.rarity;
                    }

                    int hash = getSectionHash(currentChunkPosition.x, currentChunkPosition.z, sectionIndex);
                    SectionData sectionData = SECTION_CACHE.get(hash);

                    if (sectionData != null && localPlayer.tickCount - sectionData.tickCount > 20L * ClientConfig.CACHE_KEPT_SECONDS.get()) {
                        sectionData = null;
                    }

                    boolean containsOres = sectionData != null ? sectionData.containsOres : section.maybeHas(testState -> testState.is(Tags.Blocks.ORES));
                    boolean renderOutline = rarity != OreSightEnchantment.OreRarity.NONE || containsOres;

                    if (renderOutline) {
                        foundSection = true;

                        if (rarity == null) {
                            BlockState state = currentChunk.getBlockState(mutablePosition);

                            if (state.getBlock() == Blocks.AIR) {
                                rarity = OreSightEnchantment.OreRarity.NONE;
                            } else if (state.is(AEBlockTags.ORE_SIGHT_BLACKLIST)) {
                                rarity = OreSightEnchantment.OreRarity.NONE;
                            } else if (state.is(AEBlockTags.RARE_ORE)) {
                                rarity = OreSightEnchantment.OreRarity.RARE;
                            } else if (state.is(AEBlockTags.UNCOMMON_ORE)) {
                                rarity = OreSightEnchantment.OreRarity.UNCOMMON;
                            } else if (state.is(AEBlockTags.COMMON_ORE)) {
                                rarity = OreSightEnchantment.OreRarity.COMMON;
                            } else if (state.is(Tags.Blocks.ORES)) {
                                rarity = OreSightEnchantment.OreRarity.ALL;
                            } else {
                                rarity = OreSightEnchantment.OreRarity.NONE;
                            }
                        }

                        if (rarity != OreSightEnchantment.OreRarity.NONE) {
                            float xMin = (float) (x - camera.x());
                            float yMin = (float) (y - camera.y());
                            float zMin = (float) (z - camera.z());
                            float yMax = (float) (1 + y - camera.y());
                            float xMax = (float) (1 + x - camera.x());
                            float zMax = (float) (1 + z - camera.z());

                            double xDifference = startPosition.getX() - x;
                            double yDifference = startPosition.getY() - y;
                            double zDifference = startPosition.getZ() - z;

                            double distance = (xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);

                            if (rarity.ordinal() >= configuration.oreRarity.ordinal()) {
                                if (((LevelRendererAccess) levelRenderer).getCullingFrustum().isVisible(new AABB(x, y, z, x + 1, y + 1, z + 1))) {
                                    if (distance > Mth.square(ClientConfig.GROUPED_RENDER_RANGE.get())) {
                                        Vec3 color = switch (rarity) {
                                            case ALL -> new Vec3(1, 1, 1);
                                            case COMMON -> new Vec3(0.647, 0.165, 0.165);
                                            case UNCOMMON -> new Vec3(1, 0.843, 0);
                                            case RARE -> new Vec3(0.251, 0.878, 0.816);
                                            default -> throw new IllegalStateException("Unexpected value: " + rarity);
                                        };

                                        LevelRenderer.renderLineBox(poseStack, bufferBuilder, xMin, yMin, zMin, xMax, yMax, zMax, (float) color.x(), (float) color.y(), (float) color.z(), 1);
                                    } else {
                                        collectLines(rarity, xMin, yMin, zMin, xMax, yMax, zMax);
                                    }
                                }
                            }
                        }
                    }

                    if (sectionData == null) {
                        SECTION_CACHE.put(hash, new SectionData(containsOres, localPlayer.tickCount));
                    }

                    if (blockData == null) {
                        BLOCK_CACHE.put(mutablePosition.asLong(), new BlockData(rarity, localPlayer.tickCount));
                    }

                    if (!foundSection && y != minChunkY) {
                        // Move to the next section (the bit shifting truncates the y value)
                        y = Math.max(minChunkY, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(y)));
                    }
                }

                if (!foundSection && z != maxChunkZ) {
                    // Move to the next section
                    z = Math.min(maxChunkZ, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(z) + 1));
                }
            }

            if (!foundSection && x != maxChunkX) {
                // Move to the next section
                x = Math.min(maxChunkX, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(x) + 1));
            }
        }

        drawGroupedLines(poseStack, bufferBuilder);
    }

    private static List<LineData> getLines(final OreSightEnchantment.OreRarity rarity) {
        return switch (rarity) {
            case ALL -> MISC_LINES;
            case COMMON -> COMMON_LINES;
            case UNCOMMON -> UNCOMMON_LINES;
            case RARE -> RARE_LINES;
            case NONE -> throw new IllegalStateException("Unexpected value: " + rarity);
        };
    }

    /**
     * Original ChunkPosition#hash method with y thrown into it
     */
    public static int getSectionHash(int x, int z, int y) {
        int i = 1664525 * x + 1013904223;
        int rotatedY = Integer.rotateLeft(y, 13);
        int j = 1664525 * (z ^ -559038737 ^ rotatedY) + 1013904223;
        return i ^ j;
    }

    private static void collectLines(final OreSightEnchantment.OreRarity rarity, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        int offset = 0;

        offset += collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), new Vector3f(1, 0, 0), offset);
        offset += collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), new Vector3f(0, 1, 0), offset);
        offset += collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), new Vector3f(0, 0, 1), offset);
        offset += collect(rarity, new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), new Vector3f(0, 1, 0), offset);
        offset += collect(rarity, new Vector3f(maxX, maxY, minZ), new Vector3f(minX, maxY, minZ), new Vector3f(-1, 0, 0), offset);
        offset += collect(rarity, new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), new Vector3f(0, 0, 1), offset);
        offset += collect(rarity, new Vector3f(minX, maxY, maxZ), new Vector3f(minX, minY, maxZ), new Vector3f(0, -1, 0), offset);
        offset += collect(rarity, new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), new Vector3f(1, 0, 0), offset);
        offset += collect(rarity, new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, minY, minZ), new Vector3f(0, 0, -1), offset);
        offset += collect(rarity, new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(1, 0, 0), offset);
        offset += collect(rarity, new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 1, 0), offset);
        collect(rarity, new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 0, 1), offset);
    }

    private static int collect(final OreSightEnchantment.OreRarity rarity, final Vector3f from, final Vector3f to, final Vector3f normal, int offset) {
        List<LineData> rarityLines = getLines(rarity);

        // Last added has the highest chance of fitting
        for (int i = rarityLines.size() - 1 - offset; i >= 0; i--) {
            LineData line = rarityLines.get(i);

            if (isSimilar(from, line.from) && isSimilar(to, line.to) || isSimilar(to, line.from) && isSimilar(from, line.to)) {
                rarityLines.remove(i);
                return 0;
            }
        }

        rarityLines.add(new LineData(from, to, normal));
        return 1;
    }

    private static boolean isSimilar(final Vector3f a, final Vector3f b) {
        return a.equals(b);
    }
}
