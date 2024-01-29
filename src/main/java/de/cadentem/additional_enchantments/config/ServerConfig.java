package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue PERCEPTION_SHOW_INVISIBLE;

    public static final Map<String, EnchantmentConfiguration> enchantmentConfigurations = new HashMap<>();

    private static final Map<String, Integer> ENCHANTMENTS = new HashMap<>();

    static {
        ENCHANTMENTS.put(AEEnchantments.EXPLOSIVE_TIP_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.FASTER_ATTACKS_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.HOMING_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.PLAGUE_ID, 6);
        ENCHANTMENTS.put(AEEnchantments.SHATTER_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.STRAIGHT_SHOT_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.TIPPED_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.WITHER_ID, 6);
        ENCHANTMENTS.put(AEEnchantments.PERCEPTION_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.CONFUSION_ID, 5);
        ENCHANTMENTS.put(AEEnchantments.ORE_SIGHT_ID, 5);
        ENCHANTMENTS.put(AEEnchantments.HUNTER_ID, 6);
        ENCHANTMENTS.put(AEEnchantments.BRACEWALK_ID, 4);
        ENCHANTMENTS.put(AEEnchantments.HYDRO_SHOCK_ID, 5);

        for (String enchantment : ENCHANTMENTS.keySet()) {
            BUILDER.push(enchantment);

            EnchantmentConfiguration enchantmentConfiguration = new EnchantmentConfiguration();
            enchantmentConfiguration.maxLevel = BUILDER.comment("Maximum acquirable level of the enchantment").defineInRange("max_level", ENCHANTMENTS.get(enchantment), 1, 15);
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

    public static int getDefaultMaxLevel(final String id) {
        return ENCHANTMENTS.get(id);
    }
}
