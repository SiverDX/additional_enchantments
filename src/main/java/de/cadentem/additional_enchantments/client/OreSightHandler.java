package de.cadentem.additional_enchantments.client;

import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.cadentem.additional_enchantments.capability.PlayerData;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class OreSightHandler {
    private static Map<Integer, Boolean[]> SECTION_CACHE;
    private static Map<Long, Integer> BLOCK_CACHE;
    private static int CACHE_EXPIRE = 2;

    private static final Vec3i[] COLORS = new Vec3i[]{
            new Vec3i(255, 255, 255),
            new Vec3i(165, 42, 42),
            new Vec3i(255, 215, 0),
            new Vec3i(64, 224, 208),
            new Vec3i(0, 0, 0)
    };

    @SubscribeEvent
    public static void handleOreSightEnchantment(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        if (SECTION_CACHE == null || BLOCK_CACHE == null || ClientConfig.CACHE_EXPIRE.get() != CACHE_EXPIRE) {
            CACHE_EXPIRE = ClientConfig.CACHE_EXPIRE.get();
            initCaches();
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            int enchantmentLevel = OreSightEnchantment.getClientEnchantmentLevel();

            if (enchantmentLevel > 0) {
                PlayerDataProvider.getCapability(localPlayer).ifPresent(playerData -> {
                    PoseStack poseStack = event.getPoseStack();
                    poseStack.pushPose();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.disableDepthTest();

                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tesselator.getBuilder();
                    bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

                    drawLines(localPlayer, enchantmentLevel + 1, playerData, poseStack, bufferBuilder, event.getLevelRenderer());

                    tesselator.end();
                    poseStack.popPose();
                    RenderSystem.enableDepthTest();
                    RenderType.cutout().clearRenderState();
                });
            }
        }
    }

    private static void initCaches() {
        SECTION_CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_EXPIRE, TimeUnit.SECONDS)
                .<Integer, Boolean[]>build()
                .asMap();

        BLOCK_CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_EXPIRE, TimeUnit.SECONDS)
                .<Long, Integer>build()
                .asMap();
    }

    /**
     * Referenced off of <a href="https://github.com/TelepathicGrunt/Bumblezone/blob/31cc8dc7066fc19dd0b880f66d64460cee4d356b/common/src/main/java/com/telepathicgrunt/the_bumblezone/items/essence/LifeEssence.java#L132">TelepathicGrunt</a>
     */
    private static void drawLines(final LocalPlayer localPlayer, int radius, final PlayerData playerData, final PoseStack poseStack, final BufferBuilder bufferBuilder, final LevelRenderer levelRenderer) {
        if (playerData.oreRarity == OreSightEnchantment.OreRarity.NONE) {
            return;
        }

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockPos startPosition = localPlayer.blockPosition();
        ChunkPos currentChunkPosition = new ChunkPos(startPosition);
        LevelChunk currentChunk = null;

        int minChunkX = startPosition.getX() - radius;
        int maxChunkX = startPosition.getX() + radius;
        int minChunkY = Math.max(localPlayer.level().getMinBuildHeight(), startPosition.getY() - radius);
        int maxChunkY = Math.min(localPlayer.level().getMaxBuildHeight(), startPosition.getY() + radius);
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
                    currentChunk = localPlayer.level().getChunk(sectionX, sectionZ);
                }

                foundSection = false;

                for (int y = maxChunkY; y >= minChunkY; y--) {
                    int sectionIndex = currentChunk.getSectionIndex(y);
                    LevelChunkSection section = currentChunk.getSection(sectionIndex);

                    mutablePosition.set(x, y, z);

                    if (containsOres(currentChunk, section, sectionIndex, playerData.oreRarity)) {
                        foundSection = true;

                        OreSightEnchantment.OreRarity rarity = getRarity(currentChunk, mutablePosition);

                        if (rarity != OreSightEnchantment.OreRarity.NONE) {
                            float xMin = (float) (x - camera.x());
                            float yMin = (float) (y - camera.y());
                            float zMin = (float) (z - camera.z());
                            float yMax = (float) (1 + y - camera.y());
                            float xMax = (float) (1 + x - camera.x());
                            float zMax = (float) (1 + z - camera.z());

                            if (rarity.ordinal() >= playerData.oreRarity.ordinal()) {
                                if (((LevelRendererAccess) levelRenderer).getCullingFrustum().isVisible(new AABB(x, y, z, x + 1, y + 1, z + 1))) {
                                    boolean[] renderSides = new boolean[Direction.values().length];

                                    for (Direction direction : Direction.values()) {
                                        BlockPos relative = mutablePosition.relative(direction, 1);

                                        if (isWithin(relative, minChunkX, minChunkY, minChunkZ, maxChunkX, maxChunkY, maxChunkZ)) {
                                            renderSides[direction.ordinal()] = rarity != getRarity(currentChunk, relative);
                                        } else {
                                            renderSides[direction.ordinal()] = true;
                                        }
                                    }

                                    drawLines(bufferBuilder, poseStack.last().pose(), poseStack.last().normal(), xMin, yMin, zMin, xMax, yMax, zMax, renderSides, COLORS[rarity.ordinal()]);
                                }
                            }
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

    private static boolean isWithin(final BlockPos position, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        return position.getX() >= xMin && position.getX() <= xMax && position.getY() >= yMin && position.getY() <= yMax && position.getZ() >= zMin && position.getZ() <= zMax;
    }

    private static boolean containsOres(final LevelChunk chunk, final LevelChunkSection section, int sectionIndex, final OreSightEnchantment.OreRarity configuration) {
        if (configuration == OreSightEnchantment.OreRarity.NONE) {
            return false;
        }

        Integer key = chunk.getPos().hashCode();
        Boolean[] containsOres = SECTION_CACHE.get(key);

        if (containsOres == null || containsOres[sectionIndex] == null) {
            boolean containsOre = !section.hasOnlyAir() && section.maybeHas(state -> isRelevantRarity(state, configuration));

            if (CACHE_EXPIRE > 0) {
                if (containsOres == null) {
                    containsOres = new Boolean[chunk.getSections().length];
                }

                containsOres[sectionIndex] = containsOre;
                SECTION_CACHE.put(key, containsOres);
            } else {
                return containsOre;
            }
        }

        return containsOres[sectionIndex];
    }

    private static OreSightEnchantment.OreRarity getRarity(final LevelChunk chunk, final BlockPos position) {
        Long key = position.asLong();
        Integer ordinal = BLOCK_CACHE.get(key);

        if (ordinal == null) {
            OreSightEnchantment.OreRarity rarity;

            if (isWithin(position, chunk.getPos().getMinBlockX(), position.getY(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), position.getY(), chunk.getPos().getMaxBlockZ())) {
                rarity = getRarity(chunk.getBlockState(position));
            } else {
                Player localPlayer = ClientProxy.getLocalPlayer();

                if (localPlayer != null) {
                    rarity = getRarity(localPlayer.level().getBlockState(position));
                } else {
                    return OreSightEnchantment.OreRarity.NONE;
                }
            }

            ordinal = rarity.ordinal();

            if (CACHE_EXPIRE > 0) {
                BLOCK_CACHE.put(key, ordinal);
            }
        }

        return OreSightEnchantment.OreRarity.values()[ordinal];
    }

    private static OreSightEnchantment.OreRarity getRarity(final BlockState state) {
        OreSightEnchantment.OreRarity rarity;

        if (state.isAir()) {
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

    private static boolean isRelevantRarity(final BlockState state, final OreSightEnchantment.OreRarity configuration) {
        OreSightEnchantment.OreRarity rarity = getRarity(state);

        if (rarity == OreSightEnchantment.OreRarity.NONE) {
            return false;
        }

        return rarity.ordinal() >= configuration.ordinal();
    }

    private static void drawLines(final BufferBuilder bufferBuilder, final Matrix4f lastPose, final Matrix3f normal, final float minX, float minY, float minZ, float maxX, float maxY, float maxZ, final boolean[] renderSides, final Vec3i color) {
        boolean renderNegativeY = renderSides[Direction.DOWN.ordinal()];
        boolean renderPositiveY = renderSides[Direction.UP.ordinal()];
        boolean renderNegativeZ = renderSides[Direction.NORTH.ordinal()];
        boolean renderPositiveZ = renderSides[Direction.SOUTH.ordinal()];
        boolean renderNegativeX = renderSides[Direction.WEST.ordinal()];
        boolean renderPositiveX = renderSides[Direction.EAST.ordinal()];

        if (renderNegativeY && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, normal, minX, minY, minZ, maxX, minY, minZ, 1, 0, 0, color);
        }

        if (renderNegativeX && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, normal, minX, minY, minZ, minX, maxY, minZ, 0, 1, 0, color);
        }

        if (renderNegativeX && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, normal, minX, minY, minZ, minX, minY, maxZ, 0, 0, 1, color);
        }

        if (renderPositiveX && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, normal, maxX, minY, minZ, maxX, maxY, minZ, 0, 1, 0, color);
        }

        if (renderPositiveY && renderNegativeZ) {
            drawLine(bufferBuilder, lastPose, normal, maxX, maxY, minZ, minX, maxY, minZ, -1, 0, 0, color);
        }

        if (renderPositiveY && renderNegativeX) {
            drawLine(bufferBuilder, lastPose, normal, minX, maxY, minZ, minX, maxY, maxZ, 0, 0, 1, color);
        }

        if (renderPositiveZ && renderNegativeX) {
            drawLine(bufferBuilder, lastPose, normal, minX, maxY, maxZ, minX, minY, maxZ, 0, -1, 0, color);
        }

        if (renderPositiveZ && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, normal, minX, minY, maxZ, maxX, minY, maxZ, 1, 0, 0, color);
        }

        if (renderPositiveX && renderNegativeY) {
            drawLine(bufferBuilder, lastPose, normal, maxX, minY, maxZ, maxX, minY, minZ, 0, 0, -1, color);
        }

        if (renderPositiveY && renderPositiveZ) {
            drawLine(bufferBuilder, lastPose, normal, minX, maxY, maxZ, maxX, maxY, maxZ, 1, 0, 0, color);
        }

        if (renderPositiveX && renderPositiveZ) {
            drawLine(bufferBuilder, lastPose, normal, maxX, minY, maxZ, maxX, maxY, maxZ, 0, 1, 0, color);
        }

        if (renderPositiveX && renderPositiveY) {
            drawLine(bufferBuilder, lastPose, normal, maxX, maxY, minZ, maxX, maxY, maxZ, 0, 0, 1, color);
        }
    }

    private static void drawLine(final BufferBuilder bufferBuilder, final Matrix4f lastPose, final Matrix3f normal, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, int normalX, int normalY, int normalZ, final Vec3i color) {
        bufferBuilder.vertex(lastPose, fromX, fromY, fromZ).color(color.getX(), color.getY(), color.getZ(), 255).normal(normal, normalX, normalY, normalZ).endVertex();
        bufferBuilder.vertex(lastPose, toX, toY, toZ).color(color.getX(), color.getY(), color.getZ(), 255).normal(normal, normalX, normalY, normalZ).endVertex();
    }
}
