package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class HomingEnchantment extends ConfigurableEnchantment {
    public enum TypeFilter {
        MONSTER,
        ANIMAL,
        BOSSES,
        ANY,
        NONE
    }

    public enum Priority {
        CLOSEST,
        LOWEST_HEALTH,
        HIGHEST_HEALTH,
        RANDOM
    }

    public HomingEnchantment() {
        super(Rarity.RARE, AEEnchantmentCategory.RANGED_AND_TRIDENT, EquipmentSlot.MAINHAND, AEEnchantments.HOMING_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        return other != Enchantments.RIPTIDE && super.checkCompatibility(other);
    }

    public static void setEnchantmentLevel(final Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingOwner) {
            int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.HOMING.get());

            if (level > 0) {
                ProjectileDataProvider.getCapability(projectile).ifPresent(data -> data.homingEnchantmentLevel = level);
            }
        }
    }
}
