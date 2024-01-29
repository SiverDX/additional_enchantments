package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class VoidingEnchantment extends ConfigurableEnchantment {
    public enum State {
        ENABLED,
        DISABLED
    }

    public VoidingEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, AEEnchantments.VOIDING_ID);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
