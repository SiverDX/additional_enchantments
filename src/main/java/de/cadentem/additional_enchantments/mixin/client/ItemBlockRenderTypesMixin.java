package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    /** Allow non-translucent fluids to be rendered translucent to handle the alpha change */
    @ModifyReturnValue(method = "getRenderLayer", at = @At(value = "RETURN"))
    private static RenderType additional_enchantments$handleLavaVision(final RenderType renderType, @Local(argsOnly = true) final FluidState state) {
        if (renderType == RenderType.translucent()) {
            return renderType;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is present
        boolean hasVision = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(state.getFluidType(), player.registryAccess()) != FluidVision.Mapped.NONE)
                .orElse(false);

        if (hasVision) {
            return RenderType.translucent();
        }

        return renderType;
    }
}
