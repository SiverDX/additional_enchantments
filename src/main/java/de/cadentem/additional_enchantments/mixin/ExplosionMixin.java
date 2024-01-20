package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.core.ExplosionAccess;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionAccess {
    @Unique
    boolean additional_enchantments$wasTriggeredByEnchantment;

    @Override
    public boolean additional_enchantments$wasTriggeredByEnchantment() {
        return additional_enchantments$wasTriggeredByEnchantment;
    }

    @Override
    public void additional_enchantments$setWasTriggeredByEnchantment(boolean wasTriggeredByEnchantment) {
        this.additional_enchantments$wasTriggeredByEnchantment = wasTriggeredByEnchantment;
    }
}
