package de.cadentem.additional_enchantments.enchantments.fluid_vision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedFluidVision(List<Entry> values) {
    public static final Codec<LevelBasedFluidVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedFluidVision::values)
    ).apply(instance, LevelBasedFluidVision::new));

    public static LevelBasedFluidVision constant(final FluidVision... values) {
        return new LevelBasedFluidVision(List.of(new LevelBasedFluidVision.Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<FluidVision.Mapped> get(final int level) {
        List<FluidVision.Mapped> visions = new ArrayList<>();

        for (Entry entry : values) {
            if (entry.levelRange().matches(level)) {
                entry.value().forEach(perception -> visions.add(perception.map(level)));
            }
        }

        return visions;
    }

    public record Entry(List<FluidVision> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidVision.CODEC.listOf().fieldOf("value").forGetter(Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(Entry::levelRange)
        ).apply(instance, Entry::new));
    }
}
