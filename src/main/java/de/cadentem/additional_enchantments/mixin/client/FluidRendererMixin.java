package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    /** Make fluids see-through */
    @ModifyArg(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/FluidRenderer;addFace(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFFFFFFFFFFFFIIZ)V"), index = 21)
    private int additional_enchantments$adjustFluidAlpha(final int color, @Local(argsOnly = true, name = "fluidState") final FluidState fluid) {
        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is not null
        float percentage = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(fluid.getFluidType(), player.registryAccess()).percentage())
                .orElse(1f);

        if (ARGB.alpha(color) == 0) {
            // Create fluids are set up with an alpha value of 0 - by setting the 'RenderType' to 'translucent' they become invisible due to said value
            // Therefore, when a fluid is being rendered here (and it is invisible) just set it to the intended visibility
            return Colors.overrideAlpha(color, percentage);
        }

        if (Float.compare(percentage, 1) == 0) {
            return color;
        }

        return ARGB.multiplyAlpha(color, percentage);
    }
}
