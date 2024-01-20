package de.cadentem.additional_enchantments.enchantments.config;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ConfigurableEnchantment extends Enchantment {
    protected ConfigurableEnchantment(final Rarity rarity, final EnchantmentCategory category, final EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    protected ConfigurableEnchantment(final Rarity rarity, final EnchantmentCategory category, final EquipmentSlot slot) {
        this(rarity, category, new EquipmentSlot[]{slot});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
