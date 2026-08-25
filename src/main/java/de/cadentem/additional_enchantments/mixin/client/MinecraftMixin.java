package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
    private boolean additional_enchantments$shouldEntityAppearGlowing(boolean original, @Local(argsOnly = true) final Entity entity) {
        if (original) {
            return true;
        }

        return AE.PROXY.getPerceptionColor(entity) != Colors.NONE;
    }
}
