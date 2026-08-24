package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public record Color(TextColor color, float alpha) {
    public static final Codec<Color> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextColor.CODEC.fieldOf("color").forGetter(Color::color),
            Codec.FLOAT.optionalFieldOf("alpha", 1f).forGetter(Color::alpha)
    ).apply(instance, Color::new));

    public static Color of(final ChatFormatting formatting) {
        return of(formatting, 1);
    }

    public static Color of(final ChatFormatting color, final float alpha) {
        return new Color(TextColor.fromLegacyFormat(color), alpha);
    }

    public static Color of(final String hexCode) {
        return of(hexCode, 1);
    }

    public static Color of(final String hexCode, final float alpha) {
        return new Color(TextColor.parseColor(hexCode).getOrThrow(), alpha);
    }

    public static Color of(final TextColor color) {
        return new Color(color, 1);
    }
}
