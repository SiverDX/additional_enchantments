package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class WitherEnchantment extends ConfigurableEnchantment {
    public WitherEnchantment() {
        super(Rarity.UNCOMMON, AEEnchantmentCategory.MELEE, EquipmentSlot.MAINHAND, AEEnchantments.WITHER_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        return other != AEEnchantments.PLAGUE.get() && super.checkCompatibility(other);
    }

    @Override
    public void doPostAttack(@NotNull final LivingEntity attacker, @NotNull final Entity target, int level) {
        if (target instanceof LivingEntity livingTarget && (level * ServerConfig.WITHER_CHANCE_MULTIPLIER.get()) >= attacker.getRandom().nextDouble()) {
            livingTarget.addEffect(new MobEffectInstance(AEMobEffects.WITHER.get(), 20 * (int) (ServerConfig.WITHER_DURATION_BASE.get() + (level * ServerConfig.WITHER_DURATION_MULTIPLIER.get())), level - 1));
        }
    }
}
