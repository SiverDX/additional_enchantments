package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.treasure_finder.TreasureFinder;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreasureFinderData implements ValueIOSerializable {
    // Concurrent because the worker thread (for searching) and the render thread modify it
    private final Map<Block, CacheEntry> cache = new ConcurrentHashMap<>();
    private int maximumRange = -1;

    private final Map<Identifier, TreasureFinder.Mapped> entries = new HashMap<>();

    record CacheEntry(int range, ShiftingColor.Mapped mappedColors, TreasureFinder.DisplayType displayType, int particleRate) { }

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

    public TreasureFinder.DisplayType getDisplayType(final Block block) {
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

        for (TreasureFinder.Mapped finder : entries.values()) {
            int range = finder.getRange(block);

            if (range > currentRange) {
                currentRange = range;
            }
        }

        return currentRange;
    }

    private ShiftingColor.Mapped storeMappedColors(final Block block) {
        ShiftingColor.Mapped result = ShiftingColor.Mapped.NONE;

        for (TreasureFinder.Mapped instance : entries.values()) {
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

    private TreasureFinder.DisplayType storeDisplayType(final Block block) {
        for (TreasureFinder.Mapped instance : entries.values()) {
            TreasureFinder.DisplayType displayType = instance.getDisplayType(block);

            if (displayType != TreasureFinder.DisplayType.NONE) {
                return displayType;
            }
        }

        return TreasureFinder.DisplayType.NONE;
    }

    private int storeParticleRate(final Block block) {
        for (TreasureFinder.Mapped instance : entries.values()) {
            int particleRate = instance.getParticleRate(block);

            if (particleRate != -1) {
                return particleRate;
            }
        }

        return -1;
    }

    public Collection<TreasureFinder.Mapped> getEntries() {
        return entries.values();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void setVisions(final Collection<TreasureFinder.Mapped> entries) {
        this.entries.clear();
        addVisions(entries);
    }

    public void addVisions(final Collection<TreasureFinder.Mapped> entries) {
        entries.forEach(finder -> this.entries.put(finder.id(), finder));
        invalidateCache();
    }

    public void removeVisions(final Collection<Identifier> ids) {
        ids.forEach(entries::remove);
        invalidateCache();
    }

    public void invalidateCache() {
        cache.clear();
        maximumRange = -1;
        AE.PROXY.requestCacheClear();
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", TreasureFinder.Mapped.CODEC.listOf(), entries.values().stream().toList());
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", TreasureFinder.Mapped.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
