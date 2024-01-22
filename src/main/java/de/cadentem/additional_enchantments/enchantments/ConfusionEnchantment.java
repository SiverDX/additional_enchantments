package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfusionEnchantment extends ConfigurableEnchantment {
    public ConfusionEnchantment() {
        super(Rarity.RARE, AEEnchantmentCategory.MELEE, EquipmentSlot.MAINHAND, AEEnchantments.CONFUSION_ID);
    }

    @Override
    public void doPostAttack(@NotNull final LivingEntity attacker, @NotNull final Entity target, int level) {
        if (target instanceof Mob mob && level / 10d > attacker.getRandom().nextDouble()) {
            List<LivingEntity> entities = mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(5 + level * 2), livingEntity -> {
                if (livingEntity == attacker || livingEntity == mob || livingEntity.getType().is(EntityTags.CONFUSION_BLACKLIST)) {
                    return false;
                }

                if (livingEntity.isInvisible()) {
                    return false;
                }

                return mob.canAttack(livingEntity);
            });

            LivingEntity retaliationTarget = entities.get(mob.getRandom().nextInt(entities.size()));

            mob.setLastHurtByMob(retaliationTarget);
            mob.setLastHurtByPlayer(null);
            mob.setTarget(retaliationTarget);
        }
    }
}
