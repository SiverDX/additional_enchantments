package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockVisionData implements INBTSerializable<CompoundTag> {
    // Concurrent because the worker thread (for searching) and the render thread modify it
    private final Map<Block, CacheEntry> cache = new ConcurrentHashMap<>();
    private int maximumRange = -1;

    private final Map<ResourceLocation, BlockVision.Mapped> visions = new HashMap<>();

    record CacheEntry(int range, ShiftingColor.Mapped mappedColors, BlockVision.DisplayType displayType, int particleRate) { }

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
        return cache.computeIfAbsent(block, this::storeData).mappedColors().getColor();
    }

    public List<Integer> getColors(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).mappedColors().colors();
    }

    public BlockVision.DisplayType getDisplayType(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).displayType();
    }

    public int getParticleRate(final Block block) {
        return cache.computeIfAbsent(block, this::storeData).particleRate();
    }

    private CacheEntry storeData(final Block block) {
        return new CacheEntry(storeRange(block), storeMappedColors(block), storeDisplayType(block), storeParticleRate(block));
    }

    /** If the passed state is 'null,' it will return the range as well */
    private int storeRange(@Nullable final Block block) {
        int currentRange = 0;

        for (BlockVision.Mapped vision : visions.values()) {
            int range = vision.getRange(block);

            if (range > currentRange) {
                currentRange = range;
            }
        }

        return currentRange;
    }

    private ShiftingColor.Mapped storeMappedColors(final Block block) {
        ShiftingColor.Mapped result = ShiftingColor.Mapped.NONE;

        for (BlockVision.Mapped instance : visions.values()) {
            ShiftingColor.Mapped color = instance.getMappedColors(block);

            if (color == ShiftingColor.Mapped.NONE) {
                continue;
            }

            if (result == ShiftingColor.Mapped.NONE || result.priority() < color.priority()) {
                result = color;
            }
        }

        return result;
    }

    private BlockVision.DisplayType storeDisplayType(final Block block) {
        for (BlockVision.Mapped instance : visions.values()) {
            BlockVision.DisplayType displayType = instance.getDisplayType(block);

            if (displayType != BlockVision.DisplayType.NONE) {
                return displayType;
            }
        }

        return BlockVision.DisplayType.NONE;
    }

    private int storeParticleRate(final Block block) {
        for (BlockVision.Mapped instance : visions.values()) {
            int particleRate = instance.getParticleRate(block);

            if (particleRate != -1) {
                return particleRate;
            }
        }

        return -1;
    }

    public boolean isEmpty() {
        return visions.isEmpty();
    }

    public void addVisions(final Collection<BlockVision.Mapped> visions) {
        for (BlockVision.Mapped vision : visions) {
            this.visions.put(vision.id(), vision);
        }

        invalidateCache();
    }

    public void removeVisions(final Collection<ResourceLocation> ids) {
        for (ResourceLocation id : ids) {
            visions.remove(id);
        }

        invalidateCache();
    }

    public void invalidateCache() {
        cache.clear();
        maximumRange = -1;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        BlockVision.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), visions.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("data", data));

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        visions.clear();

        BlockVision.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("data"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> visions.put(entry.id(), entry)));
    }
}
