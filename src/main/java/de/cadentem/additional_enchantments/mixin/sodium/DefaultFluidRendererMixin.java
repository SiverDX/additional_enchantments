package de.cadentem.additional_enchantments.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.util.Colors;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DefaultFluidRenderer.class)
public abstract class DefaultFluidRendererMixin {
    /** The change from 'LiquidBlockRenderer' does not apply with sodium present */
    @ModifyArg(method = "updateQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/util/ColorARGB;toABGR(I)I"))
    private int additional_enchantments$adjustAlpha(final int colorARGB, @Local(argsOnly = true, name = "fluidState") final FluidState state) {
        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is present
        float percentage = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(state.getFluidType(), player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(percentage, 1) == 0) {
            return colorARGB;
        }

        int alpha = ARGB.alpha(colorARGB);

        if (alpha == 0) {
            // Create fluids are set up with an alpha value of 0 - by setting the 'RenderType' to 'translucent' they become invisible due to said value
            // Therefore, when a fluid is being rendered here (and it is invisible) just set it to the intended visibility
            alpha = (int) (255 * percentage);
        } else {
            alpha = (int) (alpha * percentage);
        }

        return Colors.overrideAlpha(colorARGB, alpha);
    }
}
