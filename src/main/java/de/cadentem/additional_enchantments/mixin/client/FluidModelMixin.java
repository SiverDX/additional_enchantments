package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.block.FluidModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidModel.Unbaked.class)
public abstract class FluidModelMixin {
    @ModifyReturnValue(method = "getTransparency", at = @At("RETURN"))
    private static Transparency additional_enchantments$forceTranslucency(final Transparency transparency) {
        return Transparency.TRANSLUCENT;
    }
}
