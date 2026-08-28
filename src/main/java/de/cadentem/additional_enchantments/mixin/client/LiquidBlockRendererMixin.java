package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {
    /** Make the lava / water layer (more) see-through */
    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), name = "alpha")
    private float additional_enchantments$handleFluidVision(float alpha, @Local(argsOnly = true) final FluidState fluid) {
        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is not null
        float percentage = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(fluid.getFluidType(), player.registryAccess()).percentage())
                .orElse(1f);

        if (alpha == 0) {
            // Create fluids are set up with an alpha value of 0 - by setting the 'RenderType' to 'translucent' they become invisible due to said value
            // Therefore, when a fluid is being rendered here (and it is invisible) just set it to the intended visibility
            return percentage;
        }

        return alpha * percentage;
    }
}
