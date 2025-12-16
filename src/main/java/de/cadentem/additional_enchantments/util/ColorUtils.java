package de.cadentem.additional_enchantments.util;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.util.FastColor;

import java.util.List;

public class ColorUtils {
    /** Returns a color in the format of {@link net.minecraft.util.FastColor.ARGB32} */
    public static int withAlpha(int rgb, float alpha) {
        int red = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int blue = rgb & 255;
        return FastColor.ARGB32.color((int) (255f * Math.max(0, Math.min(1, alpha))), red, green, blue);
    }

    /**
     * Expects the colors in the format of {@link net.minecraft.util.FastColor.ARGB32}
     * @param speed Determines how quickly the colors are shifted through
     * @param offset Offsets the index of the color to be used (expected to be between 0 and 1)
     */
    public static int lerpColor(final List<Integer> colorsARGB, final double speed, final double offset) {
        if (colorsARGB.isEmpty()) {
            return -1;
        }

        if (colorsARGB.size() == 1) {
            return colorsARGB.get(0);
        }

        // Determine by how much % we have shifted through the color so far
        double timer = (AE.PROXY.getTimer() * speed + offset) % 1;

        if (timer < 0) {
            timer = 0;
        }

        float sizeIndex = (float) (timer * colorsARGB.size());
        int currentIndex = (int) (Math.floor(sizeIndex) % colorsARGB.size());
        int nextIndex = (currentIndex + 1) % colorsARGB.size();

        return lerp(sizeIndex - currentIndex, colorsARGB.get(currentIndex), colorsARGB.get(nextIndex));
    }

    // From FastColor in 1.21.1

    public static int lerp(float delta, int min, int max) {
        int alpha = lerpInt(delta, alpha(min), alpha(max));
        int red = lerpInt(delta, red(min), red(max));
        int green = lerpInt(delta, green(min), green(max));
        int blue = lerpInt(delta, blue(min), blue(max));
        return color(alpha, red, green, blue);
    }

    public static int lerpInt(float delta, int start, int end) {
        return start + floor(delta * (float)(end - start));
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }
    
    public static int alpha(final int argb) {
        return argb >>> 24;
    }

    public static int red(final int argb) {
        return argb >> 16 & 0xFF;
    }

    public static int green(final int argb) {
        return argb >> 8 & 0xFF;
    }

    public static int blue(final int argb) {
        return argb & 0xFF;
    }

    public static int color(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}
