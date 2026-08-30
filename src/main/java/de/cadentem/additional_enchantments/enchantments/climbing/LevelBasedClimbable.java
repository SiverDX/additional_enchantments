package de.cadentem.additional_enchantments.enchantments.climbing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedClimbable(List<Entry> values) {
    public static final Codec<LevelBasedClimbable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedClimbable::values)
    ).apply(instance, LevelBasedClimbable::new));

    public static LevelBasedClimbable constant(final Climbable... values) {
        return new LevelBasedClimbable(List.of(new Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<Climbable> get(final int level) {
        List<Climbable> predicates = new ArrayList<>();

        for (LevelBasedClimbable.Entry entry : values) {
            if (entry.levelRange().matches(level)) {
                predicates.addAll(entry.value());
            }
        }

        return predicates;
    }

    public record Entry(List<Climbable> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<LevelBasedClimbable.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Climbable.CODEC.listOf().fieldOf("value").forGetter(LevelBasedClimbable.Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(LevelBasedClimbable.Entry::levelRange)
        ).apply(instance, LevelBasedClimbable.Entry::new));
    }
}
