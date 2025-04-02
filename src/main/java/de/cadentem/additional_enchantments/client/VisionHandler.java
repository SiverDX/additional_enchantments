package de.cadentem.additional_enchantments.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.config.VisionConfig;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.OreSightEnchantment;
import de.cadentem.additional_enchantments.enchantments.TreasureFinderEnchantment;
import de.cadentem.additional_enchantments.mixin.client.FrustumAccess;
import de.cadentem.additional_enchantments.mixin.client.RandomizableContainerBlockEntityAccess;
import de.cadentem.additional_enchantments.registry.AEParticles;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class VisionHandler {
    /** Extend the search as a buffer while the background thread is searching */
    private static final int EXTENDED_SEARCH_RANGE = 16;

    private static Cache<LevelChunkSection, Boolean[]> CHUNK_CACHE;

    private static final List<Data> RENDER_DATA = new ArrayList<>();
    private static final List<Data> SEARCH_RESULT = new ArrayList<>();
    private static final List<BlockPos> REMOVAL = new ArrayList<>();

    private static int enchantmentLevel;
    private static VisionConfig.Type displayType;
    private static Vec3 lastScanCenter;

    private static boolean isSearching;
    private static boolean hasPendingUpdate;

    // Doesn't seem to work 100% of the time
    private static boolean searchedTooEarly;

    private record Data(Block block, double range, VisionConfig.Type displayType, float x, float y, float z, int color) {
        public boolean isInRange(final Vec3 position, final double visibleRange) {
            return position.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) <= visibleRange * visibleRange;
        }

        public void render(final VertexConsumer buffer, final PoseStack.Pose pose) {
            drawLines(buffer, pose, x, y, z, x + 1, y + 1, z + 1, color);
        }
    }

    @SubscribeEvent
    public static void handleBlockVision(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            return;
        }

        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        int newEnchantmentLevel = OreSightEnchantment.getClientEnchantmentLevel();
        VisionConfig.Type newDisplayType;

        if (newEnchantmentLevel == 0) {
            newEnchantmentLevel = TreasureFinderEnchantment.getClientEnchantmentLevel();
            newDisplayType = VisionConfig.Type.TREASURE_FINDER;
        } else {
            newDisplayType = VisionConfig.Type.ORE_SIGHT;
        }

        if (newEnchantmentLevel == 0) {
            clear();
            return;
        }

        double maxRange;

        if (newDisplayType == VisionConfig.Type.TREASURE_FINDER) {
            maxRange = Math.max(VisionConfig.getMaxRange(newDisplayType, newEnchantmentLevel), ServerConfig.getTreasureRange(newEnchantmentLevel));
        } else {
            maxRange = VisionConfig.getMaxRange(newDisplayType, newEnchantmentLevel);
        }

        if (maxRange == 0) {
            clear();
            return;
        } else if (displayType != null && displayType != newDisplayType) {
            // Display type changed - they might use different ranges
            clear();
        }

        enchantmentLevel = newEnchantmentLevel;
        displayType = newDisplayType;
        initCache();

        if (!isSearching && hasPendingUpdate) {
            RENDER_DATA.clear();
            RENDER_DATA.addAll(SEARCH_RESULT);
            SEARCH_RESULT.clear();

            REMOVAL.clear();
            hasPendingUpdate = false;
        }

        if (!isSearching && isOutsideRange(maxRange)) {
            lastScanCenter = player.position();
            isSearching = true;

            Util.backgroundExecutor().submit(() -> {
                collect(player, maxRange + EXTENDED_SEARCH_RANGE);
                isSearching = false;
                hasPendingUpdate = true;
            });
        }

        if (RENDER_DATA.isEmpty()) {
            return;
        }

        PoseStack pose = event.getPoseStack();
        pose.pushPose();

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pose.translate(-camera.x(), -camera.y(), -camera.z());

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (int index = 0; index < RENDER_DATA.size(); index++) {
            Data data = RENDER_DATA.get(index);

            if (wasRemoved(data)) {
                // It's more efficient to remove these here (than iterating through all current entries)
                // Since this list would usually be rather small
                RENDER_DATA.remove(index);
                index--;
                continue;
            }

            if (data.range() == 0 || !data.isInRange(player.getEyePosition(), data.range())) {
                continue;
            }

            if (((FrustumAccess) event.getFrustum()).additional_enchantments$cubeInFrustum(data.x(), data.y(), data.z(), data.x() + 1, data.y() + 1, data.z() + 1)) {
                if (data.displayType() == VisionConfig.Type.ORE_SIGHT) {
                    data.render(buffer, pose.last());
                    continue;
                }

                if (Minecraft.getInstance().isPaused()) {
                    // Newly added particles will only render once the game is un-paused
                    // Meaning if we don't skip here, all the added particles will be shown at once
                    continue;
                }

                if (data.displayType() == VisionConfig.Type.TREASURE_FINDER && player.tickCount % 10 == 0) {
                    // Increase the bounding box to make the particles more visible for blocks in walls etc.
                    double x = (data.x() + 0.5) + (player.getRandom().nextDouble() - 0.5) * 2;
                    double y = (data.y() + 0.5) + (player.getRandom().nextDouble() - 0.5) * 2;
                    double z = (data.z() + 0.5) + (player.getRandom().nextDouble() - 0.5) * 2;
                    player.level.addParticle(AEParticles.GLOW.get(), x, y, z, data.color(), enchantmentLevel, 0);
                }
            }
        }

        tesselator.end();
        pose.popPose();

        RenderSystem.enableDepthTest();
        RenderType.cutout().clearRenderState();
    }

    @SubscribeEvent
    public static void clearData(final LevelEvent.Unload event) {
        clear();
    }

    public static void updateEntry(final BlockPos position, final BlockState oldState, final BlockState newState) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || enchantmentLevel == 0) {
            return;
        }

        Block newBlock = newState.getBlock();

        if (oldState.getBlock() == newBlock) {
            // There is no block state property support
            return;
        }

        double searchRange = VisionConfig.getMaxRange(displayType, enchantmentLevel) + EXTENDED_SEARCH_RANGE;

        if (lastScanCenter != null && player.position().distanceToSqr(lastScanCenter) > searchRange * searchRange) {
            return;
        }

        VisionConfig.VisionData oldData = VisionConfig.get(displayType, enchantmentLevel, oldState.getBlock());

        if (!RENDER_DATA.isEmpty() && oldData != null && oldData.range() > 0) {
            REMOVAL.add(position);
        }

        VisionConfig.VisionData newData = VisionConfig.get(displayType, enchantmentLevel, newBlock);

        if (newData != null && newData.range() > 0) {
            RENDER_DATA.add(new Data(newBlock, newData.range(), displayType, position.getX(), position.getY(), position.getZ(), toARGB(newData.color())));
        }
    }

    public static void addTreasure(final BlockPos position, final Block block) {
        RENDER_DATA.add(new Data(block, ServerConfig.getTreasureRange(enchantmentLevel), VisionConfig.Type.TREASURE_FINDER, position.getX(), position.getY(), position.getZ(), toARGB(ServerConfig.getTreasureColor())));
    }

    public static void removeTreasure(final BlockPos position) {
        if (!RENDER_DATA.isEmpty()) {
            REMOVAL.add(position);
        }
    }

    public static int toARGB(int rgb) {
        return 0xFF << 24 | rgb;
    }

    private static void collect(final Player player, double searchRange) {
        BlockPos startPosition = player.blockPosition();
        ChunkPos currentChunkPosition = new ChunkPos(startPosition);
        LevelChunk currentChunk = null;

        int minChunkX = (int) (startPosition.getX() - searchRange);
        int maxChunkX = (int) (startPosition.getX() + searchRange);
        int minChunkY = (int) Math.max(player.level.getMinBuildHeight(), startPosition.getY() - searchRange);
        // Max build height is non-inclusive (see LevelHeightAccessor#isOutsideBuildHeight)
        int maxChunkY = (int) Math.min(player.level.getMaxBuildHeight() - 1, startPosition.getY() + searchRange);
        int minChunkZ = (int) (startPosition.getZ() - searchRange);
        int maxChunkZ = (int) (startPosition.getZ() + searchRange);

        BlockPos.MutableBlockPos mutablePosition = BlockPos.ZERO.mutable();
        boolean foundXSection = false;
        searchedTooEarly = true;

        for (int x = minChunkX; x <= maxChunkX; x++) {
            boolean foundZSection = false;

            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                int sectionX = SectionPos.blockToSectionCoord(x);
                int sectionZ = SectionPos.blockToSectionCoord(z);

                if (currentChunk == null || currentChunkPosition.x != sectionX || currentChunkPosition.z != sectionZ) {
                    currentChunkPosition = new ChunkPos(sectionX, sectionZ);
                    currentChunk = player.level.getChunk(sectionX, sectionZ);
                }

                boolean foundYSection = false;

                for (int y = maxChunkY; y >= minChunkY; y--) {
                    int sectionIndex = currentChunk.getSectionIndex(y);
                    LevelChunkSection section = currentChunk.getSection(sectionIndex);

                    mutablePosition.set(x, y, z);

                    if ((foundXSection || foundYSection || foundZSection) || hasRelevantBlock(currentChunk, section, sectionIndex)) {
                        foundXSection = true;
                        foundYSection = true;
                        foundZSection = true;

                        BlockState state = currentChunk.getBlockState(mutablePosition);

                        if (state.isAir()) {
                            continue;
                        }

                        Block block = state.getBlock();
                        VisionConfig.VisionData vision = VisionConfig.get(displayType, enchantmentLevel, block);

                        if (vision != null && vision.range() > 0) {
                            SEARCH_RESULT.add(new Data(block, vision.range(), displayType, x, y, z, toARGB(vision.color())));
                        } else if (displayType == VisionConfig.Type.TREASURE_FINDER && state.is(AEBlockTags.TREASURES) && hasLoot(player.level, new BlockPos(x, y, z))) {
                            SEARCH_RESULT.add(new Data(block, ServerConfig.getTreasureRange(enchantmentLevel), displayType, x, y, z, toARGB(ServerConfig.getTreasureColor())));
                        }
                    }

                    if (!foundYSection && y != minChunkY) {
                        // Move to the next section (the bit shifting truncates the y value)
                        y = Math.max(minChunkY, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(y)));
                    }
                }

                if (!foundZSection && z != maxChunkZ) {
                    // Move to the next section
                    z = Math.min(maxChunkZ, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(z) + 1));
                }
            }

            if (!foundXSection && x != maxChunkX) {
                // Move to the next section
                x = Math.min(maxChunkX, SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(x) + 1));
            }
        }
    }

    private static boolean hasLoot(final Level level, final BlockPos position) {
        return level.getBlockEntity(position) instanceof RandomizableContainerBlockEntityAccess access && access.additional_enchantments$getLootTable() != null;
    }

    private static boolean hasRelevantBlock(final LevelChunk chunk, final LevelChunkSection section, int sectionIndex) {
        Boolean[] cachedSection = CHUNK_CACHE.getIfPresent(section);

        if (cachedSection == null || cachedSection[sectionIndex] == null) {
            boolean containsRelevantBlock = !section.hasOnlyAir() && section.maybeHas(state -> {
                // When searching too early all sections only contain air
                searchedTooEarly = false;

                VisionConfig.VisionData vision = VisionConfig.get(displayType, enchantmentLevel, state.getBlock());

                if (vision != null && vision.range() > 0) {
                    return true;
                }

                if (displayType == VisionConfig.Type.TREASURE_FINDER) {
                    return state.is(AEBlockTags.TREASURES);
                }

                return false;
            });

            if (cachedSection == null) {
                cachedSection = new Boolean[chunk.getSections().length];
            }

            cachedSection[sectionIndex] = containsRelevantBlock;
            CHUNK_CACHE.put(section, cachedSection);
        }

        return cachedSection[sectionIndex];
    }

    private static boolean wasRemoved(final Data data) {
        for (int i = 0; i < REMOVAL.size(); i++) {
            BlockPos position = REMOVAL.get(i);

            if (position.getX() == data.x() && position.getY() == data.y() && position.getZ() == data.z()) {
                REMOVAL.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Returns 'true' if the player moved at least half the distance of their visible range
     * away from the last position that was used as the search origin for the block data
     */
    private static boolean isOutsideRange(double visibleRange) {
        if (lastScanCenter == null || searchedTooEarly) {
            return true;
        }

        //noinspection DataFlowIssue -> player is present
        Vec3 currentPosition = Minecraft.getInstance().player.position();

        double halfRange = visibleRange / 2;
        return currentPosition.distanceToSqr(lastScanCenter) > halfRange * halfRange;
    }

    private static void drawLines(final VertexConsumer buffer, final PoseStack.Pose pose, final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ, final int color) {
        drawLine(buffer, pose, minX, minY, minZ, maxX, minY, minZ, 1, 0, 0, color);
        drawLine(buffer, pose, minX, minY, minZ, minX, maxY, minZ, 0, 1, 0, color);
        drawLine(buffer, pose, minX, minY, minZ, minX, minY, maxZ, 0, 0, 1, color);
        drawLine(buffer, pose, maxX, minY, minZ, maxX, maxY, minZ, 0, 1, 0, color);
        drawLine(buffer, pose, maxX, maxY, minZ, minX, maxY, minZ, -1, 0, 0, color);
        drawLine(buffer, pose, minX, maxY, minZ, minX, maxY, maxZ, 0, 0, 1, color);
        drawLine(buffer, pose, minX, maxY, maxZ, minX, minY, maxZ, 0, -1, 0, color);
        drawLine(buffer, pose, minX, minY, maxZ, maxX, minY, maxZ, 1, 0, 0, color);
        drawLine(buffer, pose, maxX, minY, maxZ, maxX, minY, minZ, 0, 0, -1, color);
        drawLine(buffer, pose, minX, maxY, maxZ, maxX, maxY, maxZ, 1, 0, 0, color);
        drawLine(buffer, pose, maxX, minY, maxZ, maxX, maxY, maxZ, 0, 1, 0, color);
        drawLine(buffer, pose, maxX, maxY, minZ, maxX, maxY, maxZ, 0, 0, 1, color);
    }

    private static void drawLine(final VertexConsumer buffer, final PoseStack.Pose pose, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, int normalX, int normalY, int normalZ, final int color) {
        buffer.vertex(pose.pose(), fromX, fromY, fromZ).color(color).normal(pose.normal(), normalX, normalY, normalZ).endVertex();
        buffer.vertex(pose.pose(), toX, toY, toZ).color(color).normal(pose.normal(), normalX, normalY, normalZ).endVertex();
    }

    private static void clear() {
        if (CHUNK_CACHE == null) {
            // There is nothing to clean up
            return;
        }

        RENDER_DATA.clear();
        SEARCH_RESULT.clear();
        REMOVAL.clear();

        lastScanCenter = null;

        isSearching = false;
        hasPendingUpdate = false;

        CHUNK_CACHE.invalidateAll();
        CHUNK_CACHE = null;
    }

    private static void initCache() {
        if (CHUNK_CACHE == null) {
            CHUNK_CACHE = CacheBuilder.newBuilder()
                    .expireAfterWrite(5, TimeUnit.SECONDS)
                    .concurrencyLevel(1)
                    .build();
        }
    }
}
