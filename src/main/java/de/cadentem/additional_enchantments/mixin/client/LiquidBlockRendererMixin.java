package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {
    /** Make the lava / water layer (more) see-through */
//    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), ordinal = 0)
//    private float additional_enchantments$handleFluidVision(float alpha, @Local(argsOnly = true) final FluidState fluid, @Local(name = "flag") final boolean isLava) {
//        return alpha * 0.35f;
//
//        return alpha;
//    }
}
