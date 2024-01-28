package de.cadentem.additional_enchantments.mixin.forge;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
    private boolean additional_enchantments$doImpact(final AbstractArrow instance, final HitResult hitResult) {
        AtomicBoolean result = new AtomicBoolean(true);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            ProjectileDataProvider.getCapability(instance).ifPresent(data -> {
                if (data.explosiveTipEnchantmentLevel > 0) {
                    result.set(false);
                }
            });
        }

        return result.get();
    }
}
