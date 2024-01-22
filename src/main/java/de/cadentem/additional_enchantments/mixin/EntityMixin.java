package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 750)
public abstract class EntityMixin {
    /**
     * Clean up references to other objects
     */
    @Inject(method = "remove", at = @At("HEAD"))
    private void additional_enchantments$clearReferences(final Entity.RemovalReason reason, final CallbackInfo callback) {
        HomingEnchantment.clearHomingContext((Entity) (Object) this);
    }
}
