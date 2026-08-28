package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedHoming(List<Entry> values) {
    public static final Codec<LevelBasedHoming> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedHoming::values)
    ).apply(instance, LevelBasedHoming::new));

    public static LevelBasedHoming constant(final Homing... values) {
        return new LevelBasedHoming(List.of(new Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<Homing.Mapped> get(final int level) {
        List<Homing.Mapped> values = new ArrayList<>();

        for (LevelBasedHoming.Entry entry : this.values) {
            if (entry.levelRange().matches(level)) {
                entry.value().forEach(perception -> values.add(perception.map(level)));
            }
        }

        return values;
    }

    public record Entry(List<Homing> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<LevelBasedHoming.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Homing.CODEC.listOf().fieldOf("value").forGetter(LevelBasedHoming.Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(LevelBasedHoming.Entry::levelRange)
        ).apply(instance, LevelBasedHoming.Entry::new));
    }
}
