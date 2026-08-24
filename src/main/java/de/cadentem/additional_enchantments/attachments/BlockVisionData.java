package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import de.cadentem.additional_enchantments.util.Functions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockVisionData implements INBTSerializable<CompoundTag> {
    // Concurrent because the worker thread (for searching) and the render thread modify it
    private final Map<Block, CacheEntry> cache = new ConcurrentHashMap<>();
    private int maximumRange = -1;

    private @Nullable List<BlockVision> visions;

    record CacheEntry(int range, List<Integer> colors, BlockVision.DisplayType displayType, int particleRate, double colorShiftRate) { }

    public int getRange(@Nullable final Block block) {
        if (block == null) {
            if (maximumRange == -1) {
                maximumRange = storeRange(null);
            }

            return maximumRange;
        }

        return cache.computeIfAbsent(block, this::storeData).range();
    }

    public int getColor(final Block block) {
        return Functions.lerpColor(cache.computeIfAbsent(block, this::storeData).colors(), cache.computeIfAbsent(block, this::storeData).colorShiftRate(), 0);
    }

    public List<Integer> getColors(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).colors();
    }

    public BlockVision.DisplayType getDisplayType(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).displayType();
    }

    public int getParticleRate(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).particleRate();
    }

    private CacheEntry storeData(final Block block) {
        return new CacheEntry(storeRange(block), storeColor(block), storeDisplayType(block), storeParticleRate(block), storeColorShiftRate(block));
    }

    /** If the passed state is 'null,' it will return the range as well */
    private int storeRange(@Nullable final Block block) {
        int currentRange = 0;

        if (visions == null) {
            return 0;
        }

        for (BlockVision vision : visions) {
            int range = vision.getRange(block);

            if (range > currentRange) {
                currentRange = range;
            }
        }

        return currentRange;
    }

    private @Unmodifiable List<Integer> storeColor(final Block block) {
        if (visions == null) {
            return List.of();
        }

        for (BlockVision instance : visions) {
            List<Integer> colors = instance.getColors(block);

            if (!colors.isEmpty()) {
                return colors;
            }
        }

        return List.of();
    }

    private BlockVision.DisplayType storeDisplayType(final Block block) {
        if (visions == null) {
            return BlockVision.DisplayType.NONE;
        }

        for (BlockVision instance : visions) {
            BlockVision.DisplayType displayType = instance.getDisplayType(block);

            if (displayType != BlockVision.DisplayType.NONE) {
                return displayType;
            }
        }

        return BlockVision.DisplayType.NONE;
    }

    private int storeParticleRate(final Block block) {
        if (visions == null) {
            return -1;
        }

        for (BlockVision instance : visions) {
            int particleRate = instance.getParticleRate(block);

            if (particleRate != -1) {
                return particleRate;
            }
        }

        return -1;
    }

    private double storeColorShiftRate(final Block block) {
        if (visions == null) {
            return -1;
        }

        for (BlockVision instance : visions) {
            double colorShiftRate = instance.getColorShiftRate(block);

            if (colorShiftRate != -1) {
                return colorShiftRate;
            }
        }

        return -1;
    }

    public void setVision(@Nullable final List<BlockVision> visions) {
        List<BlockVision> previous = this.visions;

        if (visions == null || visions.isEmpty()) {
            this.visions = null;
            invalidateCache();
        } else {
            this.visions = visions;

            if (!this.visions.equals(previous)) {
                invalidateCache();
            }
        }
    }

    public int size() {
        return visions == null ? 0 : visions.size();
    }

    public void invalidateCache() {
        cache.clear();
        maximumRange = -1;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (visions != null) {
            BlockVision.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), visions)
                    .resultOrPartial(AE.LOG::error).ifPresent(data -> tag.put("visions", data));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        visions = null;

        if (tag.contains("visions")) {
            visions = BlockVision.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.getList("visions", Tag.TAG_COMPOUND))
                    .resultOrPartial(AE.LOG::error).orElse(null);
        }
    }
}
