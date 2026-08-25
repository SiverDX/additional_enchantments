package de.cadentem.additional_enchantments.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record ShiftingColor(List<Color> colors, double colorShiftRate) {
    public static final Codec<ShiftingColor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.listOf().fieldOf("colors").forGetter(ShiftingColor::colors),
            Codec.DOUBLE.optionalFieldOf("color_shift_rate", 1.0).forGetter(ShiftingColor::colorShiftRate)
    ).apply(instance, ShiftingColor::new));

    public static ShiftingColor of(final List<Color> colors) {
        return of(colors, 1);
    }

    public static ShiftingColor of(final List<Color> colors, final double colorShiftRate) {
        return new ShiftingColor(colors, colorShiftRate);
    }

    public Mapped map() {
        return new Mapped(colors.stream().map(color -> Colors.withAlpha(color.color().getValue(), color.alpha())).toList(), colorShiftRate);
    }

    public record Mapped(List<Integer> colors, double colorShiftRate) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.listOf().fieldOf("colors").forGetter(Mapped::colors),
                Codec.DOUBLE.optionalFieldOf("color_shift_rate", 1.0).forGetter(Mapped::colorShiftRate)
        ).apply(instance, Mapped::new));

        public static final Mapped NONE = new Mapped(List.of(), 1);

        public int getColor() {
            return Functions.lerpColor(colors, colorShiftRate, 0);
        }
    }
}
