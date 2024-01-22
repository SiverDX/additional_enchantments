package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue PERCEPTION_SHOW_INVISIBLE;

    public static final Map<String, EnchantmentConfiguration> enchantmentConfigurations = new HashMap<>();

    public static int DEFAULT_MAX_LEVEL = 4;

    static {
        List<String> enchantments = List.of(
                AEEnchantments.EXPLOSIVE_TIP_ID,
                AEEnchantments.FASTER_ATTACKS_ID,
                AEEnchantments.HOMING_ID,
                AEEnchantments.POISON_ID,
                AEEnchantments.SHATTER_ID,
                AEEnchantments.STRAIGHT_SHOT_ID,
                AEEnchantments.TIPPED_ID,
                AEEnchantments.WITHER_ID,
                AEEnchantments.PERCEPTION_ID
        );

        for (String enchantment : enchantments) {
            BUILDER.push(enchantment);

            int maxLevel = DEFAULT_MAX_LEVEL;

            if (enchantment.equals(AEEnchantments.POISON_ID) || enchantment.equals(AEEnchantments.WITHER_ID)) {
                maxLevel = 6;
            }

            EnchantmentConfiguration enchantmentConfiguration = new EnchantmentConfiguration();
            enchantmentConfiguration.maxLevel = BUILDER.comment("Maximum acquirable level of the enchantment").defineInRange("min_level", maxLevel, 1, 10);
            enchantmentConfiguration.isEnabled = BUILDER.comment("Enable or disable the enchantment completely").define("is_enabled", true);
            enchantmentConfiguration.isDiscoverable = BUILDER.comment("Determines if this enchantment can appear in loot / the enchantment table etc.").define("is_discoverable", true);
            enchantmentConfiguration.isAllowedOnBooks = BUILDER.comment("Allow the enchantment to be applied to books").define("is_allowed_on_books", true);
            enchantmentConfiguration.isTradeable = BUILDER.comment("Determines if the enchantment appears in trades").define("is_tradeable", true);
            enchantmentConfiguration.isTreasure = BUILDER.comment("Treasure enchantments usually do not appear in the enchantment table").define("is_treasure", false);

            switch (enchantment) {
                case AEEnchantments.PERCEPTION_ID -> PERCEPTION_SHOW_INVISIBLE = BUILDER.comment("Enable / Disable outlines on invisible entities").define("perception_show_invisible", true);
            }

            enchantmentConfigurations.put(enchantment, enchantmentConfiguration);

            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }
}
