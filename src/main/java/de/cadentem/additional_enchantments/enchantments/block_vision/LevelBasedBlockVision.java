package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.ArrayList;
import java.util.List;

public record LevelBasedBlockVision(List<Entry> values) {
    public static final Codec<LevelBasedBlockVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedBlockVision::values)
    ).apply(instance, LevelBasedBlockVision::new));

    public static LevelBasedBlockVision constant(final BlockVision... values) {
        return new LevelBasedBlockVision(List.of(new Entry(List.of(values), MinMaxBounds.Ints.atLeast(0))));
    }

    public List<BlockVision.Mapped> get(final int level) {
        List<BlockVision.Mapped> visions = new ArrayList<>();

        for (Entry entry : values) {
            if (entry.levelRange().matches(level)) {
                entry.value().forEach(vision -> visions.add(vision.map(level)));
            }
        }

        return visions;
    }

    public record Entry(List<BlockVision> value, MinMaxBounds.Ints levelRange) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockVision.CODEC.listOf().fieldOf("value").forGetter(Entry::value),
                MinMaxBounds.Ints.CODEC.fieldOf("level_range").forGetter(Entry::levelRange)
        ).apply(instance, Entry::new));
    }
}
