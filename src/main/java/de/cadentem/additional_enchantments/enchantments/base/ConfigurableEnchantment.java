package de.cadentem.additional_enchantments.enchantments.base;

import de.cadentem.additional_enchantments.config.EnchantmentConfiguration;
import de.cadentem.additional_enchantments.config.ServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableEnchantment extends Enchantment {
    protected final String id;

    protected ConfigurableEnchantment(final Rarity rarity, final EnchantmentCategory category, final EquipmentSlot[] slots, final String id) {
        super(rarity, category, slots);
        this.id = id;
    }

    protected ConfigurableEnchantment(final Rarity rarity, final EnchantmentCategory category, final EquipmentSlot slot, final String id) {
        this(rarity, category, new EquipmentSlot[]{slot}, id);
    }

    @Override
    public int getMaxLevel() {
        return ServerConfig.SPEC.isLoaded() ? ServerConfig.enchantmentConfigurations.get(id).maxLevel.get() : ServerConfig.getDefaultMaxLevel(id);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull final ItemStack stack) {
        return (ServerConfig.SPEC.isLoaded() ? ServerConfig.enchantmentConfigurations.get(id).isEnabled.get() : true) && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean canEnchant(@NotNull final ItemStack stack) {
        return (ServerConfig.SPEC.isLoaded() ? ServerConfig.enchantmentConfigurations.get(id).isEnabled.get() : true) && super.canEnchant(stack);
    }

    @Override
    public boolean isDiscoverable() {
        if (ServerConfig.SPEC.isLoaded()) {
            EnchantmentConfiguration enchantmentConfiguration = ServerConfig.enchantmentConfigurations.get(id);
            return enchantmentConfiguration.isEnabled.get() && enchantmentConfiguration.isDiscoverable.get();
        }

        return super.isDiscoverable();
    }

    @Override
    public boolean isAllowedOnBooks() {
        if (ServerConfig.SPEC.isLoaded()) {
            EnchantmentConfiguration enchantmentConfiguration = ServerConfig.enchantmentConfigurations.get(id);
            return enchantmentConfiguration.isEnabled.get() && enchantmentConfiguration.isAllowedOnBooks.get();
        }

        return super.isAllowedOnBooks();
    }

    @Override
    public boolean isTradeable() {
        if (ServerConfig.SPEC.isLoaded()) {
            EnchantmentConfiguration enchantmentConfiguration = ServerConfig.enchantmentConfigurations.get(id);
            return enchantmentConfiguration.isEnabled.get() && enchantmentConfiguration.isTradeable.get();
        }

        return super.isTradeable();
    }

    @Override
    public boolean isTreasureOnly() {
        return ServerConfig.SPEC.isLoaded() ? ServerConfig.enchantmentConfigurations.get(id).isTreasure.get() : super.isTreasureOnly();
    }
}
