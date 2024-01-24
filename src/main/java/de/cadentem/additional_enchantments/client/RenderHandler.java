package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.config.ClientConfig;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.OreSightEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.antlr.v4.runtime.misc.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler {
    private static final Map<OreSightEnchantment.OreRarity, List<Triple<Vector3f, Vector3f, Vector3f>>> LINES = new HashMap<>();

    private static BufferBuilder bufferBuilder;
    private static PoseStack poseStack;

    @SubscribeEvent
    public static void handleOreSightEnchantment(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            int enchantmentLevel = OreSightEnchantment.getClientEnchantmentLevel();

            if (enchantmentLevel > 0) {
                poseStack = event.getPoseStack();
                poseStack.pushPose();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.disableDepthTest();

                Tesselator tesselator = Tesselator.getInstance();
                bufferBuilder = tesselator.getBuilder();
                bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

                drawLines(localPlayer, enchantmentLevel + 1);

                tesselator.end();
                poseStack.popPose();
                RenderSystem.enableDepthTest();
                RenderType.cutout().clearRenderState();

                bufferBuilder = null;
                poseStack = null;
            }
        }
    }

    private static void drawLines() {
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

            List<Triple<Vector3f, Vector3f, Vector3f>> rarityLines = LINES.get(rarity);

            for (Triple<Vector3f, Vector3f, Vector3f> line : rarityLines) {
                bufferBuilder.vertex(poseStack.last().pose(), line.a.x(), line.a.y(), line.a.z()).color(color.getX(), color.getY(), color.getZ(), 255).normal(line.c.x(), line.c.y(), line.c.z()).endVertex();
                bufferBuilder.vertex(poseStack.last().pose(), line.b.x(), line.b.y(), line.b.z()).color(color.getX(), color.getY(), color.getZ(), 255).normal(line.c.x(), line.c.y(), line.c.z()).endVertex();
            }

            rarityLines.clear();
        }
    }

    /**
     * Referenced off of <a href="https://github.com/TelepathicGrunt/Bumblezone/blob/31cc8dc7066fc19dd0b880f66d64460cee4d356b/common/src/main/java/com/telepathicgrunt/the_bumblezone/items/essence/LifeEssence.java#L132">TelepathicGrunt</a>
     */
    private static void drawLines(final LocalPlayer localPlayer, int radius) {
        ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
            if (configuration.oreRarity == OreSightEnchantment.OreRarity.NONE) {
                return;
            }

            for (OreSightEnchantment.OreRarity rarity : OreSightEnchantment.OreRarity.values()) {
                LINES.put(rarity, new ArrayList<>());
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
                        LevelChunkSection section = currentChunk.getSection(currentChunk.getSectionIndex(y));
                        // TODO :: check lowest y block to see if we're still in the section, to avoid re-checking the section?
                        if (section.maybeHas(state -> state.is(Tags.Blocks.ORES))) {
                            foundSection = true;

                            mutablePosition.set(x, y, z);
                            BlockState state = currentChunk.getBlockState(mutablePosition);

                            if (state.is(AEBlockTags.ORE_SIGHT_BLACKLIST)) {
                                continue;
                            }

                            if (state.is(Tags.Blocks.ORES)) {
                                float xMin = (float) (x - camera.x());
                                float yMin = (float) (y - camera.y());
                                float zMin = (float) (z - camera.z());
                                float xMax = (float) (1 + x - camera.x());
                                float yMax = (float) (1 + y - camera.y());
                                float zMax = (float) (1 + z - camera.z());

                                double xDifference = startPosition.getX() - x;
                                double yDifference = startPosition.getY() - y;
                                double zDifference = startPosition.getZ() - z;

                                double distance = (xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);

                                OreSightEnchantment.OreRarity rarity = OreSightEnchantment.OreRarity.ALL;

                                if (state.is(AEBlockTags.COMMON_ORE)) {
                                    rarity = OreSightEnchantment.OreRarity.COMMON;
                                } else if (state.is(AEBlockTags.UNCOMMON_ORE)) {
                                    rarity = OreSightEnchantment.OreRarity.UNCOMMON;
                                } else if (state.is(AEBlockTags.RARE_ORE)) {
                                    rarity = OreSightEnchantment.OreRarity.RARE;
                                }

                                if (rarity.ordinal() < configuration.oreRarity.ordinal()) {
                                    continue;
                                }

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
                        } else if (y != minChunkY) {
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

            drawLines();
        });
    }

    private static void collectLines(final OreSightEnchantment.OreRarity rarity, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), new Vector3f(1, 0, 0));
        collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), new Vector3f(0, 1, 0));
        collect(rarity, new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), new Vector3f(0, 0, 1));
        collect(rarity, new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), new Vector3f(0, 1, 0));
        collect(rarity, new Vector3f(maxX, maxY, minZ), new Vector3f(minX, maxY, minZ), new Vector3f(-1, 0, 0));
        collect(rarity, new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), new Vector3f(0, 0, 1));
        collect(rarity, new Vector3f(minX, maxY, maxZ), new Vector3f(minX, minY, maxZ), new Vector3f(0, -1, 0));
        collect(rarity, new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), new Vector3f(1, 0, 0));
        collect(rarity, new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, minY, minZ), new Vector3f(0, 0, -1));
        collect(rarity, new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(1, 0, 0));
        collect(rarity, new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 1, 0));
        collect(rarity, new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 0, 1));
    }

    private static void collect(final OreSightEnchantment.OreRarity rarity, final Vector3f from, final Vector3f to, final Vector3f normal) {
        List<Triple<Vector3f, Vector3f, Vector3f>> rarityLines = LINES.get(rarity);

        for (Triple<Vector3f, Vector3f, Vector3f> line : rarityLines) {
            Vector3f knownFrom = line.a;
            Vector3f knownTo = line.b;

            if (isSimilar(from, knownFrom) && isSimilar(to, knownTo) || isSimilar(to, knownFrom) && isSimilar(from, knownTo)) {
                rarityLines.remove(line);
                return;
            }
        }

        rarityLines.add(new Triple<>(from, to, normal));
    }

    private static final double DERIVATION = 0.01;

    private static boolean isSimilar(final Vector3f a, final Vector3f b) {
        if (a.x() == b.x() && a.y() == b.y() && a.z() == b.z()) {
            return true;
        }

        return Mth.abs(a.x() - b.x()) <= DERIVATION && Mth.abs(a.y() - b.y()) <= DERIVATION && Mth.abs(a.z() - b.z()) <= DERIVATION;
    }
}
