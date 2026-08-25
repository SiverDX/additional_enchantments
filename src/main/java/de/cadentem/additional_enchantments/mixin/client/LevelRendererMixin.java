package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.Colors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Definition(id = "getTeamColor", method = "Lnet/minecraft/world/entity/Entity;getTeamColor()I")
    @Expression("? = ?.getTeamColor()")
    @ModifyVariable(method = "renderLevel", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private int additional_enchantments$getTypeColor(final int teamColor, @Local final Entity entity) {
        if (teamColor != /* ChatFormatting#WHITE */ 16777215) {
            // For compatibility use the already modified color (if present)
            return teamColor;
        }

        int color = AE.PROXY.getPerceptionColor(entity);

        if (color == Colors.NONE) {
            return teamColor;
        }

        return color;
    }
}
