package de.cadentem.additional_enchantments.core.interfaces;

import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;

public interface ProjectileAccess {
    HomingEnchantment.HomingContext additional_enchantments$getHomingContext();
    void additional_enchantments$setHomingContext(final HomingEnchantment.HomingContext homingContext);
    int additional_enchantments$getExplosiveTipEnchantmentLevel();
    void additional_enchantments$setExplosiveTipEnchantmentLevel(int explosiveTipEnchantmentLevel);
}
