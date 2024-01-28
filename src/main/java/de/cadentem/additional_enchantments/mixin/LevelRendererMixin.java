package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.data.AEEntityTags;
import de.cadentem.additional_enchantments.enchantments.PerceptionEnchantment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LevelRenderer.class, priority = 1500)
public abstract class LevelRendererMixin {
    @ModifyVariable(method = "renderLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I", shift = At.Shift.AFTER))
    private int additional_enchantments$getTypeColor(int teamColor, @Local final Entity entity) {
        if (teamColor == /* Default color (WHITE) */ 16777215 && PerceptionEnchantment.getClientEnchantmentLevel() > 0 && !entity.getType().is(AEEntityTags.PERCEPTION_BLACKLIST)) {
            if (entity.getType().is(Tags.EntityTypes.BOSSES)) {
                return /* DARK_PURPLE */ 11141290;
            } else if (entity instanceof Monster) {
                return /* RED */ 16733525;
            } else if (entity instanceof Animal) {
                return entity instanceof TamableAnimal ? /* DARK_GREEN */ 43520 : /* GREEN */ 5635925;
            } else if (entity instanceof ItemEntity item) {
                TextColor textColor = item.getItem().getDisplayName().getStyle().getColor();
                return textColor != null ? textColor.getValue() : /* GOLD */ 16755200;
            } else {
                return /* BLUE */ 5592575;
            }
        }

        return teamColor;
    }
}
