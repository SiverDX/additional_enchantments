package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final Map<String, EnchantmentConfiguration> enchantmentConfigurations = new HashMap<>();

    static {
        List<String> enchantments = List.of(
                AEEnchantments.EXPLOSIVE_TIP_ID,
                AEEnchantments.FASTER_ATTACKS_ID,
                AEEnchantments.HOMING_ID,
                AEEnchantments.SHATTER_ID,
                AEEnchantments.STRAIGHT_SHOT_ID,
                AEEnchantments.TIPPED_ID
        );

        for (String enchantment : enchantments) {
            BUILDER.push(enchantment);

            EnchantmentConfiguration enchantmentConfiguration = new EnchantmentConfiguration();
            enchantmentConfiguration.maxLevel = BUILDER.comment("Maximum acquirable level of the enchantment").defineInRange("min_level", 3, 1, 10);
            enchantmentConfiguration.isEnabled = BUILDER.comment("Enable or disable the enchantment").define("is_enabled", true);
            enchantmentConfiguration.isAllowedOnBooks = BUILDER.comment("Allow the enchantment to be applied to books").define("is_allowed_on_books", true);
            enchantmentConfiguration.isTradeable = BUILDER.comment("Determines if the enchantment appears in trades").define("is_tradeable", true);
            enchantmentConfiguration.isTreasure = BUILDER.comment("Treasure enchantments usually do not appear in the enchantment table").define("is_treasure", true);

            enchantmentConfigurations.put(enchantment, enchantmentConfiguration);

            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }
}
