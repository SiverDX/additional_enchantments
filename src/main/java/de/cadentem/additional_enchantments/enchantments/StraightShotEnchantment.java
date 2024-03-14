package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class StraightShotEnchantment extends ConfigurableEnchantment {
    public StraightShotEnchantment() {
        super(Rarity.UNCOMMON, AEEnchantmentCategory.RANGED_AND_TRIDENT, EquipmentSlot.MAINHAND, AEEnchantments.STRAIGHT_SHOT_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        return other != Enchantments.RIPTIDE && super.checkCompatibility(other);
    }

    public static void updateGravity(final Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingOwner) {
            int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.STRAIGHT_SHOT.get());

            if (level > 0) {
                ProjectileDataProvider.getCapability(projectile).ifPresent(data -> {
                    projectile.setNoGravity(true);

                    data.straightShotEnchantmentLevel = level;
                    data.gravityTime = 20 * ServerConfig.GRAVITY_SECONDS.get();
                });
            }
        }
    }
}
