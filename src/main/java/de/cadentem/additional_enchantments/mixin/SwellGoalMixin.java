package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(SwellGoal.class)
public class SwellGoalMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void skipHunterTarget(final CallbackInfo callback) {
        if (target instanceof Player player) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), player);

            if (enchantmentLevel > 0) {
                ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                    if (configuration.hasMaxHunterStacks(enchantmentLevel)) {
                        target = null;
                    }
                });
            }
        }
    }

    @Shadow @Nullable private LivingEntity target;
}
