package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.core.LivingEntityAccess;
import de.cadentem.additional_enchantments.core.ProjectileAccess;
import de.cadentem.additional_enchantments.enchantments.config.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.config.EnchantmentCategories;
import de.cadentem.additional_enchantments.registry.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber
public class HomingEnchantment extends ConfigurableEnchantment {
    public enum TypeFilter {
        MONSTER,
        ANIMAL,
        BOSSES,
        ANY
    }

    public enum Priority {
        CLOSEST,
        LOWEST_HEALTH,
        HIGHEST_HEALTH,
        RANDOM
    }

    public HomingEnchantment() {
        super(Rarity.RARE, EnchantmentCategories.RANGED, EquipmentSlot.MAINHAND);
    }

    @SubscribeEvent
    public static void markProjectile(final EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof LivingEntity livingOwner) {
                int level = livingOwner.getMainHandItem().getEnchantmentLevel(Enchantments.HOMING.get());

                if (level > 0) {
                    ((ProjectileAccess) projectile).additional_enchantments$setHomingContext(new HomingContext(null, level));
                }
            }
        }
    }

    @SubscribeEvent
    public static void unmarkProjectile(final ProjectileImpactEvent event) {
        clearHomingContext(event.getEntity());
    }

    public static void clearHomingContext(final Entity entity) {
        if (entity instanceof Projectile projectile) {
            HomingContext context = ((ProjectileAccess) projectile).additional_enchantments$getHomingContext();

            if (context != null && context.target != null) {
                ((LivingEntityAccess) context.target).additional_enchantments$removeTracked();
            }

            ((ProjectileAccess) projectile).additional_enchantments$setHomingContext(null);
        }
    }

    public static class HomingContext {
        @Nullable
        public LivingEntity target;
        public int enchantmentLevel;

        public HomingContext(@Nullable final LivingEntity target, int enchantmentLevel) {
            this.target = target;
            this.enchantmentLevel = enchantmentLevel;
        }
    }
}
