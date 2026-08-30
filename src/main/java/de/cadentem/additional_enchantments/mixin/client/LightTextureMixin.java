package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightTextureMixin {
    /** Increase brightness when water vision is active */
    @ModifyExpressionValue(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1))
    private boolean additional_enchantments$adjustWaterBrightness(boolean original, @Local(name = "player") final LocalPlayer player, @Local(name = "waterVision") final float waterVision) {
        if (original) {
            return true;
        }

        // TODO :: make this accessible and use in case a mod adds a mixin in there
//        EntityFluidInteraction.getFluidTypeByTag(FluidTags.WATER);

        if (waterVision > 0) {
            return player.getExistingData(AEDataAttachments.FLUID_VISION)
                    .map(vision -> vision.get(NeoForgeMod.WATER_TYPE) != FluidVision.Mapped.NONE)
                    .orElse(false);
        }

        return false;
    }
}
