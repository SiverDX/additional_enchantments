package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
    /**
     * Target gets selected server-side<br>
     * Movement gets handled on both sides
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void additional_enchantments$handleHomingEnchantment(final CallbackInfo callback) {
        Projectile instance = (Projectile) (Object) this;

        ProjectileDataProvider.getCapability(instance).ifPresent(data -> {
            if (data.straightShotEnchantmentLevel > 0) {
                if (instance.isNoGravity() && data.gravityTime == 0) {
                    instance.setNoGravity(false);
                } else {
                    data.gravityTime--;
                }
            }

            if (data.homingEnchantmentLevel == 0) {
                return;
            }

            data.handleHomingMovement(instance);
            data.searchForHomingTarget(instance);
        });
    }
}
