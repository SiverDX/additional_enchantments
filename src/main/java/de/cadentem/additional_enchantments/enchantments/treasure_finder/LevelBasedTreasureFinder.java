package de.cadentem.additional_enchantments.enchantments.treasure_finder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedTreasureFinder(List<Entry> values) {
    public static final Codec<LevelBasedTreasureFinder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedTreasureFinder::values)
    ).apply(instance, LevelBasedTreasureFinder::new));

    public static LevelBasedTreasureFinder constant(final TreasureFinder... values) {
        return new LevelBasedTreasureFinder(List.of(new Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<TreasureFinder.Mapped> get(final int level) {
        List<TreasureFinder.Mapped> visions = new ArrayList<>();

        for (Entry entry : values) {
            if (entry.levelRange().matches(level)) {
                entry.value().forEach(vision -> visions.add(vision.map(level)));
            }
        }

        return visions;
    }

    public record Entry(List<TreasureFinder> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TreasureFinder.CODEC.listOf().fieldOf("value").forGetter(Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(Entry::levelRange)
        ).apply(instance, Entry::new));
    }
}
