package de.cadentem.additional_enchantments.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantmentUtils {
    public static int getLevel(@NotNull final LivingEntity entity, @NotNull final ResourceKey<Enchantment> enchantment) {
        return entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(reference -> EnchantmentHelper.getEnchantmentLevel(reference, entity)).orElse(0);
    }

    public static int getLevel(@NotNull final Level level, @NotNull final ResourceKey<Enchantment> enchantment, @NotNull final ItemStack stack) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment).map(stack::getEnchantmentLevel).orElse(0);
    }
}
