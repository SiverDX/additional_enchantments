package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.core.interfaces.ProjectileAccess;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class StraightShotEnchantment extends ConfigurableEnchantment {
    public StraightShotEnchantment() {
        super(Rarity.UNCOMMON, AEEnchantmentCategory.RANGED, EquipmentSlot.MAINHAND, AEEnchantments.STRAIGHT_SHOT_ID);
    }

    @SubscribeEvent
    public static void updateGravity(final EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof LivingEntity livingOwner) {
                int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.STRAIGHT_SHOT.get());

                if (level > 0) {
                    projectile.setNoGravity(true);
                    ((ProjectileAccess) projectile).additional_enchantments$setStraightShotEnchantmentLevel(level);
                }
            }
        }
    }
}
