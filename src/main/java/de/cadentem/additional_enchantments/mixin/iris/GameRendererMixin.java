package de.cadentem.additional_enchantments.mixin.iris;

import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    /**
     * Inject at HEAD because Iris / Oculus will willingly run into a {@link NullPointerException} in case the entity does not have night vision </br>
     * Shaders may use this value to adjust the visibility within fluids (usually water)
     */
    @Inject(method = "getNightVisionScale", at = @At(value = "HEAD"), cancellable = true)
    private static void additional_enchantments$modifyNightVisionScale(final LivingEntity entity, float nanoTime, final CallbackInfoReturnable<Float> callback) {
        FluidType fluid = entity.getFirstEyeInFluidType();

        if (fluid != NeoForgeMod.WATER_TYPE.value()) {
            return;
        }

        boolean hasVision =entity.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(fluid, entity.registryAccess()) != FluidVision.Mapped.NONE)
                .orElse(false);

        if (hasVision) {
            callback.setReturnValue(1f);
        }
    }
}