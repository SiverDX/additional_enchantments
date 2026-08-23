package de.cadentem.additional_enchantments.compat;

import net.irisshaders.iris.api.v0.IrisApi;

public class Compat {
    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public static boolean isShaderActive() {
        if (ModID.IRIS.isLoaded() && IrisApi.getInstance().isShaderPackInUse()) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public static boolean isRenderingShadows() {
        if (ModID.IRIS.isLoaded() && IrisApi.getInstance().isRenderingShadowPass()) {
            return true;
        }

        return false;
    }
}
