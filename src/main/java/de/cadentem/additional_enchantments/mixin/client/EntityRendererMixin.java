package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
    private int additional_enchantments$getPerceptionColor(final int teamColor, @Local(argsOnly = true, name = "entity") final T entity) {
        ShiftingColor.Mapped color = AE.PROXY.getPerceptionColor(entity);

        if (color == ShiftingColor.Mapped.NONE) {
            return teamColor;
        }

        return color.getColor();
    }
}
