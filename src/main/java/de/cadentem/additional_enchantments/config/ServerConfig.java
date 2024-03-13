package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Perception
    public static ForgeConfigSpec.BooleanValue PERCEPTION_SHOW_INVISIBLE;

    // Tipped
    public static ForgeConfigSpec.DoubleValue TIPPED_DURATION_BASE;
    public static ForgeConfigSpec.DoubleValue TIPPED_DURATION_MULTIPLIER;

    // Wither
    public static ForgeConfigSpec.DoubleValue WITHER_CHANCE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue WITHER_DURATION_BASE;
    public static ForgeConfigSpec.DoubleValue WITHER_DURATION_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue WITHER_DAMAGE_BASE;
    public static ForgeConfigSpec.DoubleValue WITHER_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.IntValue WITHER_DAMAGE_TICK_RATE;

    // Plague
    public static ForgeConfigSpec.DoubleValue PLAGUE_CHANCE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue PLAGUE_DURATION_BASE;
    public static ForgeConfigSpec.DoubleValue PLAGUE_DURATION_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue PLAGUE_DAMAGE_BASE;
    public static ForgeConfigSpec.DoubleValue PLAGUE_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.IntValue PLAGUE_DAMAGE_TICK_RATE;

    // Hunter
    public static ForgeConfigSpec.IntValue HUNTER_STACK_REDUCTION;

    // Explosive Tip
    public static ForgeConfigSpec.DoubleValue EXPLOSIVE_TIP_RADIUS_MULTIPLIER;

    // Faster Attacks
    public static ForgeConfigSpec.DoubleValue FASTER_ATTACKS_MULTIPLIER;

    // Shatter
    public static ForgeConfigSpec.DoubleValue SHATTER_CHANCE_BASE;
    public static ForgeConfigSpec.DoubleValue SHATTER_CHANCE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SHATTER_DAMAGE_MULTIPLIER;

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
        ENCHANTMENTS.put(AEEnchantments.VOIDING_ID, 1);

        for (String enchantment : ENCHANTMENTS.keySet()) {
            BUILDER.push(enchantment);

            EnchantmentConfiguration enchantmentConfiguration = new EnchantmentConfiguration();

            if (ENCHANTMENTS.get(enchantment) > 1) {
                enchantmentConfiguration.maxLevel = BUILDER.comment("Maximum acquirable level of the enchantment").defineInRange("max_level", ENCHANTMENTS.get(enchantment), 1, 15);
            }

            enchantmentConfiguration.isEnabled = BUILDER.comment("Enable or disable the enchantment completely").define("is_enabled", true);
            enchantmentConfiguration.isDiscoverable = BUILDER.comment("Determines if this enchantment can appear in loot / the enchantment table etc.").define("is_discoverable", true);
            enchantmentConfiguration.isAllowedOnBooks = BUILDER.comment("Allow the enchantment to be applied to books").define("is_allowed_on_books", true);
            enchantmentConfiguration.isTradeable = BUILDER.comment("Determines if the enchantment appears in trades").define("is_tradeable", true);
            enchantmentConfiguration.isTreasure = BUILDER.comment("Treasure enchantments usually do not appear in the enchantment table").define("is_treasure", false);

            switch (enchantment) {
                case AEEnchantments.PERCEPTION_ID -> PERCEPTION_SHOW_INVISIBLE = BUILDER.comment("Enable / Disable outlines on invisible entities").define("perception_show_invisible", true);
                case AEEnchantments.TIPPED_ID -> {
                    TIPPED_DURATION_BASE = BUILDER.comment("Base duration (in seconds) for the applied effect").defineInRange("tipped_duration_base", 3d, 0d, 60d);
                    TIPPED_DURATION_MULTIPLIER = BUILDER.comment("How much the enchantment level affect the duration (1 * <multiplier>) (result will be in seconds)").defineInRange("tipped_duration_multiplier", 2d, 0d, 10d);
                }
                case AEEnchantments.WITHER_ID -> {
                    WITHER_CHANCE_MULTIPLIER = BUILDER.comment("Chance for the effect to apply (level * <chance_multiplier>) (result of 1 means 100%)").defineInRange("wither_chance_multiplier", 0.1d, 0d, 1d);
                    WITHER_DURATION_BASE = BUILDER.comment("Base duration (in seconds) for the applied effect").defineInRange("wither_duration_base", 3d, 0d, 60d);
                    WITHER_DURATION_MULTIPLIER = BUILDER.comment("How much the enchantment level affects the duration (1 * <multiplier>) (result will be in seconds)").defineInRange("wither_duration_multiplier", 2d, 0d, 10d);
                    WITHER_DAMAGE_BASE = BUILDER.comment("Base damage for the wither effect (the effect level (amplifier) gets added to this damage)").defineInRange("wither_damage_base", 1d, 0d, 100d);
                    WITHER_DAMAGE_MULTIPLIER = BUILDER.comment("Multiplier to the (<base_damage> + amplifier) result").defineInRange("wither_damage_multiplier", 0.75d, 0d, 10d);
                    WITHER_DAMAGE_TICK_RATE = BUILDER.comment("Determines how often the wither effect ticks (i.e. deals damage) - when (duration % (Math.max(1, 20 - amplifier / <damage_tick_rate>))) is equal to 0 the effect tick will happen").defineInRange("wither_damage_tick_rate", 2, 1, 15);
                }
                case AEEnchantments.PLAGUE_ID -> {
                    PLAGUE_CHANCE_MULTIPLIER = BUILDER.comment("Chance for the effect to apply (level * <chance_multiplier>) (result of 1 means 100%)").defineInRange("plague_chance_multiplier", 0.1d, 0d, 1d);
                    PLAGUE_DURATION_BASE = BUILDER.comment("Base duration (in seconds) for the applied effect").defineInRange("plague_duration_base", 3d, 0d, 60d);
                    PLAGUE_DURATION_MULTIPLIER = BUILDER.comment("How much the enchantment level affects the duration (1 * <multiplier>) (result will be in seconds)").defineInRange("plague_duration_multiplier", 2d, 0d, 10d);
                    PLAGUE_DAMAGE_BASE = BUILDER.comment("Base damage for the plague effect (the effect level (amplifier) gets added to this damage)").defineInRange("plague_damage_base", 1d, 0d, 100d);
                    PLAGUE_DAMAGE_MULTIPLIER = BUILDER.comment("Multiplier to the (<base_damage> + amplifier) result").defineInRange("plague_damage_multiplier", 0.5d, 0d, 10d);
                    PLAGUE_DAMAGE_TICK_RATE = BUILDER.comment("Determines how often the plague effect ticks (i.e. deals damage) - when (duration % (Math.max(1, 20 - amplifier / <damage_tick_rate>))) is equal to 0 the effect tick will happen").defineInRange("plague_damage_tick_rate", 3, 1, 15);
                }
                case AEEnchantments.HUNTER_ID -> HUNTER_STACK_REDUCTION = BUILDER.comment("Determines how much enchantment levels affect the speed of losing stacks - when (tick_count % (level / <reduction>)) is equal to 0 the stacks will reduce by 1").defineInRange("hunter_stack_reduction", 3, 1, 15);
                case AEEnchantments.EXPLOSIVE_TIP_ID -> EXPLOSIVE_TIP_RADIUS_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level which will determine the explosion radius (level * <multiplier>) (result will at least by 0.1)").defineInRange("explosive_tip_radius_multiplier", 1d, 0d, 100d);
                case AEEnchantments.FASTER_ATTACKS_ID -> FASTER_ATTACKS_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level which will determine the attack speed bonus (level * <multiplier>) (result will apply as multiply_base)").defineInRange("faster_attacks_multiplier", 0.15d, 0d, 10d);
                case AEEnchantments.SHATTER_ID -> {
                    SHATTER_CHANCE_BASE = BUILDER.comment("Base chance for the projectile to shatter and deal area of effect damage (1 means 100%)").defineInRange("shatter_chance_base", 0.3d, 0d, 1d);
                    SHATTER_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level to determine the bonus to the base chance (level * <chance_multiplier>)").defineInRange("shatter_chance_modifier", 0.1d, 0d, 1d);
                    SHATTER_DAMAGE_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level to determine the area of effect damage (level * <damage_multiplier>)").defineInRange("shatter_damage_multiplier", 0.5d, 0d, 10d);
                }
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
