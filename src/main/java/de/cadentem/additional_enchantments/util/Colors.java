package de.cadentem.additional_enchantments.util;

public class Colors {
    public static final int NONE = -1;

    public static int withAlpha(int rgb, float alpha) {
        int a = (int) (255 * alpha);
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    public static int overrideAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | (alpha << 24);
    }

    public static int overrideAlpha(int argb, float alpha) {
        int a = (int) (255 * alpha);
        return (argb & 0x00FFFFFF) | (a << 24);
    }
}
