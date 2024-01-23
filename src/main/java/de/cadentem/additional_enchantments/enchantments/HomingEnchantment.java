package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.CapabilityHandler;
import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        super(Rarity.RARE, AEEnchantmentCategory.RANGED, EquipmentSlot.MAINHAND, AEEnchantments.HOMING_ID);
    }

    public static void setEnchantmentLevel(final Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingOwner) {
            int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.HOMING.get());

            if (level > 0) {
                ProjectileDataProvider.getCapability(projectile).ifPresent(data -> data.homingEnchantmentLevel = level);
            }
        }
    }

    @SubscribeEvent
    public static void unmarkProjectile(final ProjectileImpactEvent event) {
        clearHomingTarget(event.getEntity());
    }

    public static void clearHomingTarget(final Entity entity) {
        if (entity instanceof Projectile projectile) {
            ProjectileDataProvider.getCapability(projectile).ifPresent(data -> {
                data.homingTarget = null;
                CapabilityHandler.syncProjectileData(projectile);
            });
        }
    }
}
