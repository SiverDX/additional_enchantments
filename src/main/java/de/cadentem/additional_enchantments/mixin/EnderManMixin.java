package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {
    protected EnderManMixin(final EntityType<? extends Monster> type, final Level level) {
        super(type, level);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getDirectEntity()Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER), cancellable = true)
    private void additional_enchantments$damageByArrow(final DamageSource source, float amount, final CallbackInfoReturnable<Boolean> callback) {
        if (source.getDirectEntity() instanceof Projectile projectile) {
            ProjectileDataProvider.getCapability(projectile).ifPresent(data -> {
                if (data.straightShotEnchantmentLevel * 0.2 > getRandom().nextDouble()) {
                    callback.setReturnValue(super.hurt(source, amount));
                }
            });
        }
    }
}
