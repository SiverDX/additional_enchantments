package de.cadentem.additional_enchantments.util;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.util.FastColor;

import java.util.List;

public class Functions {
    /** See {@link Functions#lerpColor(List, double, double)} */
    public static int lerpColor(final List<Integer> colorsARGB) {
        return lerpColor(colorsARGB, 1, 0);
    }

    /**
     * Expects the colors in the format of {@link net.minecraft.util.FastColor.ARGB32}
     * @param speed Determines how quickly the colors are shifted through
     * @param offset Offsets the index of the color to be used (expected to be between 0 and 1)
     */
    public static int lerpColor(final List<Integer> colorsARGB, final double speed, final double offset) {
        if (colorsARGB.isEmpty()) {
            return Colors.NONE;
        }

        if (colorsARGB.size() == 1) {
            return colorsARGB.getFirst();
        }

        // Determine by how much % we have shifted through the color so far
        double timer = (AE.PROXY.getTimer() * speed + offset) % 1;

        if (timer < 0) {
            timer = 0;
        }

        float sizeIndex = (float) (timer * colorsARGB.size());
        int currentIndex = (int) (Math.floor(sizeIndex) % colorsARGB.size());
        int nextIndex = (currentIndex + 1) % colorsARGB.size();

        return FastColor.ARGB32.lerp(sizeIndex - currentIndex, colorsARGB.get(currentIndex), colorsARGB.get(nextIndex));
    }
}
