package de.cadentem.additional_enchantments.enchantments.perception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedPerception(List<Entry> values) {
    public static final Codec<LevelBasedPerception> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedPerception::values)
    ).apply(instance, LevelBasedPerception::new));

    public static LevelBasedPerception constant(final Perception... values) {
        return new LevelBasedPerception(List.of(new Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<Perception.Mapped> get(final int level) {
        List<Perception.Mapped> predicates = new ArrayList<>();

        for (LevelBasedPerception.Entry entry : values) {
            if (entry.levelRange().matches(level)) {
                entry.value().forEach(perception -> predicates.add(perception.map(level)));
            }
        }

        return predicates;
    }

    public record Entry(List<Perception> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<LevelBasedPerception.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Perception.CODEC.listOf().fieldOf("value").forGetter(LevelBasedPerception.Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(LevelBasedPerception.Entry::levelRange)
        ).apply(instance, LevelBasedPerception.Entry::new));
    }
}
