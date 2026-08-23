package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record LevelBasedBlockVision(List<Entry> values) {
    public static final Codec<LevelBasedBlockVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("values").forGetter(LevelBasedBlockVision::values)
    ).apply(instance, LevelBasedBlockVision::new));

    public static LevelBasedBlockVision constant(final BlockVision... values) {
        return new LevelBasedBlockVision(List.of(new Entry(List.of(values), 0)));
    }

    public static LevelBasedBlockVision atLevel(final int level, final BlockVision... values) {
        return new LevelBasedBlockVision(List.of(new Entry(List.of(values), level)));
    }

    public @Unmodifiable List<BlockVision> get(final int level) {
        for (Entry entry : values) {
            if (level >= entry.fromLevel()) {
                return entry.value();
            }
        }

        return List.of();
    }

    public record Entry(List<BlockVision> value, int fromLevel) implements Comparable<Entry> {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockVision.CODEC.listOf().fieldOf("value").forGetter(Entry::value),
                ExtraCodecs.intRange(0, Integer.MAX_VALUE).fieldOf("from_level").forGetter(Entry::fromLevel)
        ).apply(instance, Entry::new));

        @Override
        public int compareTo(@NotNull final Entry other) {
            if (fromLevel < other.fromLevel()) {
                return -1;
            } else if (fromLevel > other.fromLevel()) {
                return 1;
            }

            return 0;
        }
    }
}
