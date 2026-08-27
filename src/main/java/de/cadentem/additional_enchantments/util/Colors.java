package de.cadentem.additional_enchantments.util;

import net.minecraft.util.FastColor;

public class Colors {
    public static final int NONE = -1;

    /** Returns a color in the format of {@link FastColor.ARGB32} */
    public static int withAlpha(int rgb, float alpha) {
        return FastColor.ARGB32.color((int) (255 * alpha), rgb);
    }
}
