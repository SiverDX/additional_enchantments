package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    /** Increase brightness when water vision is active */
    @ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1))
    private boolean additional_enchantments$handleFluidVision(boolean original, @Local(name = "f6") final float waterVision) {
        if (original) {
            return true;
        }

        if (waterVision > 0) {
            //noinspection DataFlowIssue -> player is present
            return Minecraft.getInstance().player.getExistingData(AEDataAttachments.FLUID_VISION)
                    .map(vision -> vision.get(NeoForgeMod.WATER_TYPE) != FluidVision.Mapped.NONE)
                    .orElse(false);
        }

        return false;
    }
}
