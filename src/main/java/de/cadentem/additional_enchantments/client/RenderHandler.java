package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
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
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
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
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler {
    private static long CACHE_TIME;

    private static final Map<Integer, SectionData> SECTION_CACHE = new WeakHashMap<>();
    private static final Map<Long, BlockData> BLOCK_CACHE = new WeakHashMap<>();

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

        CACHE_TIME = 20L * ClientConfig.CACHE_KEPT_SECONDS.get();
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

    /**
     * Referenced off of <a href="https://github.com/TelepathicGrunt/Bumblezone/blob/31cc8dc7066fc19dd0b880f66d64460cee4d356b/common/src/main/java/com/telepathicgrunt/the_bumblezone/items/essence/LifeEssence.java#L132">TelepathicGrunt</a>
     */
    private static void collectAndDrawLines(final LocalPlayer localPlayer, int radius, final Configuration configuration, final PoseStack poseStack, final BufferBuilder bufferBuilder, final LevelRenderer levelRenderer) {
        if (configuration.oreRarity == OreSightEnchantment.OreRarity.NONE) {
            return;
        }

        radius = 30;

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

                    OreSightEnchantment.OreRarity rarity = null;
                    SectionData sectionData = null;
                    BlockData blockData = null;
                    int hash = 0;

                    if (CACHE_TIME > 0) {
                        blockData = getBlockCacheEntry(mutablePosition, localPlayer.tickCount);

                        if (blockData != null) {
                            rarity = blockData.rarity;
                        }

                        hash = getSectionHash(currentChunkPosition.x, currentChunkPosition.z, sectionIndex);
                        sectionData = SECTION_CACHE.get(hash);

                        if (sectionData != null && localPlayer.tickCount - sectionData.tickCount > CACHE_TIME) {
                            SECTION_CACHE.remove(hash);
                            sectionData = null;
                        }
                    }

                    boolean containsOres = sectionData != null ? sectionData.containsOres : !section.hasOnlyAir() && section.maybeHas(testState -> testState.is(Tags.Blocks.ORES));
                    boolean renderOutline = rarity != OreSightEnchantment.OreRarity.NONE || containsOres;

                    if (renderOutline) {
                        foundSection = true;

                        if (rarity == null) {
                            rarity = getRarity(currentChunk, mutablePosition, localPlayer.tickCount);
                        }

                        if (rarity != OreSightEnchantment.OreRarity.NONE) {
                            float xMin = (float) (x - camera.x());
                            float yMin = (float) (y - camera.y());
                            float zMin = (float) (z - camera.z());
                            float yMax = (float) (1 + y - camera.y());
                            float xMax = (float) (1 + x - camera.x());
                            float zMax = (float) (1 + z - camera.z());

                            if (rarity.ordinal() >= configuration.oreRarity.ordinal()) {
                                if (((LevelRendererAccess) levelRenderer).getCullingFrustum().isVisible(new AABB(x, y, z, x + 1, y + 1, z + 1))) {
                                    boolean[] renderSides = new boolean[Direction.values().length];

                                    for (Direction direction : Direction.values()) {
                                        BlockPos relative = mutablePosition.relative(direction, 1);

                                        if (relative.getX() >= minChunkX && relative.getX() <= maxChunkX && relative.getY() >= minChunkY && relative.getY() <= maxChunkY && relative.getZ() >= minChunkZ && relative.getZ() <= maxChunkZ) {
                                            renderSides[direction.ordinal()] = rarity != getRarity(currentChunk, relative, localPlayer.tickCount);
                                        } else {
                                            // The other block is outside the [Ore Sight] radius
                                            renderSides[direction.ordinal()] = true;
                                        }
                                    }

                                    Vec3i color = switch (rarity) {
                                        case ALL -> new Vec3i(255, 255, 255);
                                        case COMMON -> new Vec3i(165, 42, 42);
                                        case UNCOMMON -> new Vec3i(255, 215, 0);
                                        case RARE -> new Vec3i(64, 224, 208);
                                        default -> throw new IllegalStateException("Unexpected value: " + rarity);
                                    };

                                    drawLines(bufferBuilder, poseStack.last().pose(), xMin, yMin, zMin, xMax, yMax, zMax, renderSides, color);
                                }
                            }
                        }
                    }

                    if (CACHE_TIME > 0) {
                        if (sectionData == null) {
                            SECTION_CACHE.put(hash, new SectionData(containsOres, localPlayer.tickCount));
                        }

                        if (containsOres && blockData == null) {
                            BLOCK_CACHE.put(mutablePosition.asLong(), new BlockData(rarity, localPlayer.tickCount));
                        }
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
    }

    private static @Nullable BlockData getBlockCacheEntry(final BlockPos position, int tickCount) {
        if (CACHE_TIME > 0) {
            long key = position.asLong();

            BlockData blockData = BLOCK_CACHE.get(key);

            if (blockData != null && tickCount - blockData.tickCount > CACHE_TIME) {
                BLOCK_CACHE.remove(key);
                blockData = null;
            }

            return blockData;
        }

        return null;
    }

    private static OreSightEnchantment.OreRarity getRarity(final LevelChunk chunk, final BlockPos position, int tickCount) {
        BlockData blockData = getBlockCacheEntry(position, tickCount);

        if (blockData == null) {
            OreSightEnchantment.OreRarity rarity = getRarity(chunk.getBlockState(position));

            if (CACHE_TIME > 0) {
                BLOCK_CACHE.put(position.asLong(), new BlockData(rarity, tickCount));
            }

            return rarity;
        }

        return blockData.rarity;
    }

    private static OreSightEnchantment.OreRarity getRarity(final BlockState state) {
        OreSightEnchantment.OreRarity rarity;

        // TODO :: cache for ore types?
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

        return rarity;
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

    private static void drawLines(final BufferBuilder bufferBuilder, final Matrix4f lastPose, final float minX, float minY, float minZ, float maxX, float maxY, float maxZ, final boolean[] renderSides, final Vec3i color) {
        boolean renderNegativeY = renderSides[Direction.DOWN.ordinal()];
        boolean renderPositiveY = renderSides[Direction.UP.ordinal()];
        boolean renderNegativeZ = renderSides[Direction.NORTH.ordinal()];
        boolean renderPositiveZ = renderSides[Direction.SOUTH.ordinal()];
        boolean renderNegativeX = renderSides[Direction.WEST.ordinal()];
        boolean renderPositiveX = renderSides[Direction.EAST.ordinal()];

        if (renderNegativeY && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, minX, minY, minZ, maxX, minY, minZ, 1, 0, 0, color);
        }

        if (renderNegativeX && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, minX, minY, minZ, minX, maxY, minZ, 0, 1, 0, color);
        }

        if (renderNegativeX && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, minX, minY, minZ, minX, minY, maxZ, 0, 0, 1, color);
        }

        if (renderPositiveX && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, maxX, minY, minZ, maxX, maxY, minZ, 0, 1, 0, color);
        }

        if (renderPositiveY && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, maxX, maxY, minZ, minX, maxY, minZ, -1, 0, 0, color);
        }

        if (renderPositiveY && renderNegativeX) {
            drawLine(bufferBuilder, lastPose, minX, maxY, minZ, minX, maxY, maxZ, 0, 0, 1, color);
        }

        if (renderPositiveZ && renderNegativeX) {
            drawLine(bufferBuilder, lastPose, minX, maxY, maxZ, minX, minY, maxZ, 0, -1, 0, color);
        }

        if (renderPositiveZ && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, minX, minY, maxZ, maxX, minY, maxZ, 1, 0, 0, color);
        }

        if (renderPositiveX && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, maxX, minY, maxZ, maxX, minY, minZ, 0, 0, -1, color);
        }

        if (renderPositiveY && renderPositiveZ) {
            drawLine(bufferBuilder, lastPose, minX, maxY, maxZ, maxX, maxY, maxZ, 1, 0, 0, color);
        }

        if (renderPositiveX && renderPositiveZ) {
            drawLine(bufferBuilder, lastPose, maxX, minY, maxZ, maxX, maxY, maxZ, 0, 1, 0, color);
        }

        if (renderPositiveX && renderPositiveY) {
            drawLine(bufferBuilder, lastPose, maxX, maxY, minZ, maxX, maxY, maxZ, 0, 0, 1, color);
        }
    }

    private static void drawLine(final BufferBuilder bufferBuilder, final Matrix4f lastPose, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, int normalX, int normalY, int normalZ, final Vec3i color) {
        bufferBuilder.vertex(lastPose, fromX, fromY, fromZ).color(color.getX(), color.getY(), color.getZ(), 255).normal(normalX, normalY, normalZ).endVertex();
        bufferBuilder.vertex(lastPose, toX, toY, toZ).color(color.getX(), color.getY(), color.getZ(), 255).normal(normalX, normalY, normalZ).endVertex();
    }
}
