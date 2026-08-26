package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    /** Immediate full visibility in water when water vision is active (+ relevant for brightness increase) */
    @ModifyExpressionValue(method = "getWaterVision", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;waterVisionTime:I", ordinal = 0))
    private int dragonSurvival$handleWaterVision(int original) {
        if (VisionHandler.hasWaterVision()) {
            return 600;
        }

        return original;
    }
}
