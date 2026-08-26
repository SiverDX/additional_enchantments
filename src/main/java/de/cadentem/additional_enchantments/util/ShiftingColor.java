package de.cadentem.additional_enchantments.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record ShiftingColor(List<Color> colors, double colorShiftRate, int priority) {
    public static final Codec<ShiftingColor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.listOf().fieldOf("colors").forGetter(ShiftingColor::colors),
            Codec.DOUBLE.optionalFieldOf("color_shift_rate", 1.0).forGetter(ShiftingColor::colorShiftRate),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ShiftingColor::priority)
    ).apply(instance, ShiftingColor::new));

    public static ShiftingColor of(final List<Color> colors) {
        return of(colors, 1, 0);
    }

    public static ShiftingColor of(final List<Color> colors, final double colorShiftRate) {
        return new ShiftingColor(colors, colorShiftRate, 0);
    }

    public static ShiftingColor of(final List<Color> colors, final double colorShiftRate, int priority) {
        return new ShiftingColor(colors, colorShiftRate, priority);
    }

    public Mapped map() {
        return new Mapped(colors.stream().map(color -> Colors.withAlpha(color.color().getValue(), color.alpha())).toList(), colorShiftRate, priority);
    }

    public record Mapped(List<Integer> colors, double colorShiftRate, int priority) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.listOf().fieldOf("colors").forGetter(Mapped::colors),
                Codec.DOUBLE.optionalFieldOf("color_shift_rate", 1.0).forGetter(Mapped::colorShiftRate),
                Codec.INT.optionalFieldOf("priority", 0).forGetter(Mapped::priority)
        ).apply(instance, Mapped::new));

        public static final Mapped NONE = new Mapped(List.of(), 1, 0);

        public int getColor() {
            return Functions.lerpColor(colors, colorShiftRate, 0);
        }
    }
}
