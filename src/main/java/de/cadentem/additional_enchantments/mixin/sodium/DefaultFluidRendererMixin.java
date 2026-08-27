package de.cadentem.additional_enchantments.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DefaultFluidRenderer.class)
public abstract class DefaultFluidRendererMixin {
    /** The change from 'LiquidBlockRenderer' does not apply with sodium present */
    @ModifyArg(method = "updateQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/util/ColorARGB;toABGR(I)I"))
    private int additional_enchantments$applyVisionAlpha(final int colorARGB, @Local(argsOnly = true) final FluidState state) {
        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is present
        float percentage = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(state.getFluidType(), player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(percentage, 1) == 0) {
            return colorARGB;
        }

        return FastColor.ARGB32.color((int) (FastColor.ABGR32.alpha(colorARGB) * percentage), colorARGB);
    }
}
