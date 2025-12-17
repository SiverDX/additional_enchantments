package de.cadentem.additional_enchantments.config;

import com.electronwill.nightconfig.core.Config;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Perception
    public static ForgeConfigSpec.BooleanValue PERCEPTION_SHOW_INVISIBLE;

    // Tipped
    public static ForgeConfigSpec.DoubleValue TIPPED_DURATION_BASE;
    public static ForgeConfigSpec.DoubleValue TIPPED_DURATION_MULTIPLIER;
    public static ForgeConfigSpec.BooleanValue TIPPED_SCALE_COOLDOWN_WITH_LEVEL;
    public static ForgeConfigSpec.IntValue TIPPED_COOLDOWN;

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

    // Straight Shot
    public static ForgeConfigSpec.IntValue GRAVITY_SECONDS;

    // Faster Attacks
    public static ForgeConfigSpec.DoubleValue FASTER_ATTACKS_MULTIPLIER;

    // Shatter
    public static ForgeConfigSpec.DoubleValue SHATTER_CHANCE_BASE;
    public static ForgeConfigSpec.DoubleValue SHATTER_CHANCE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SHATTER_DAMAGE_MULTIPLIER;

    // Treasure Finder
    public static ForgeConfigSpec.IntValue TREASURE_FINDER_PARTICLE_RATE;

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
        ENCHANTMENTS.put(AEEnchantments.TREASURE_FINDER_ID, 4);
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
                    TIPPED_SCALE_COOLDOWN_WITH_LEVEL = BUILDER.comment("Determines if the cooldown should scale with enchantment level (i.e. increasing it)").define("tipped_scale_cooldown_with_level", true);
                    TIPPED_COOLDOWN = BUILDER.comment("The cooldown (in ticks) before another arrow can apply its effects on the hit entity").defineInRange("tipped_cooldown", 0, 0, /* 1 hour */ 20 * 60 * 60);
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
                case AEEnchantments.STRAIGHT_SHOT_ID -> GRAVITY_SECONDS = BUILDER.comment("The amount of seconds the projectile will have no gravity for - this is to prevent tridents from never returning e.g.").defineInRange("gravity_seconds", 10, 0, 60);
                case AEEnchantments.FASTER_ATTACKS_ID -> FASTER_ATTACKS_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level which will determine the attack speed bonus (level * <multiplier>) (result will apply as multiply_base)").defineInRange("faster_attacks_multiplier", 0.15d, 0d, 10d);
                case AEEnchantments.SHATTER_ID -> {
                    SHATTER_CHANCE_BASE = BUILDER.comment("Base chance for the projectile to shatter and deal area of effect damage (1 means 100%)").defineInRange("shatter_chance_base", 0.3d, 0d, 1d);
                    SHATTER_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level to determine the bonus to the base chance (level * <chance_multiplier>)").defineInRange("shatter_chance_modifier", 0.1d, 0d, 1d);
                    SHATTER_DAMAGE_MULTIPLIER = BUILDER.comment("Multiplier to the enchantment level to determine the area of effect damage (level * <damage_multiplier>)").defineInRange("shatter_damage_multiplier", 0.5d, 0d, 10d);
                }
                case AEEnchantments.TREASURE_FINDER_ID -> {
                    TREASURE_FINDER_PARTICLE_RATE = BUILDER.comment("How often the particles will spawn (in ticks)").defineInRange("particle_rate", 10, 1, 1000);
                    String lineOne = "See default entries for reference\n";
                    String lineTwo = "'resource' can be a block or block tag ('$treasure' relates to containers that have yet to generate their loot)\n";
                    String lineThree = "'from_level' and 'to_level' describe the bounds for which enchantment level the effect will be shown ('-1' means no limit)\n";
                    String lineFour = "'color' can be a known text color from Minecraft or one in the '#123456' format";
                    VisionConfig.RAW_ENTRIES = BUILDER.comment(lineOne + lineTwo + lineThree + lineFour).defineList("entries", buildDefaultVisionEntries(), VisionConfig.ParsedEntry::validate);
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

    private static List<Config> buildDefaultVisionEntries() {
        List<Config> list = new ArrayList<>();

        BiFunction<String, Double, Config> colorEntry = (color, alpha) -> {
            Config config = Config.inMemory();
            config.set("color", color);
            config.set("alpha", alpha);
            return config;
        };

        Function<Object[], Config> entry = args -> {
            Config config = Config.inMemory();
            config.set("resource", args[0]);
            config.set("from_level", args[1]);
            config.set("to_level", args[2]);
            config.set("range", args[3]);
            config.set("colors", args[4]);
            config.set("color_shift_rate", args[5]);
            config.set("display_type", args[6]);
            return config;
        };

        list.add(entry.apply(new Object[]{VisionConfig.SpecialBlock.TREASURE.getKey(), 1, -1, 24.0, List.of(
                colorEntry.apply("gold", 0.15),
                colorEntry.apply("yellow", 0.15),
                colorEntry.apply("#ffdd55", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_COPPER.location(), 1, 1, 24.0, List.of(
                colorEntry.apply("#7a4a2e", 0.15),
                colorEntry.apply("dark_green", 0.15),
                colorEntry.apply("#3f7f5f", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_IRON.location(), 1, 2, 24.0, List.of(
                colorEntry.apply("white", 0.15),
                colorEntry.apply("gray", 0.15),
                colorEntry.apply("dark_gray", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_REDSTONE.location(), 1, 2, 16.0, List.of(
                colorEntry.apply("dark_red", 0.15),
                colorEntry.apply("red", 0.15),
                colorEntry.apply("#ff4d4d", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_LAPIS.location(), 2, 3, 16.0, List.of(
                colorEntry.apply("dark_blue", 0.15),
                colorEntry.apply("blue", 0.15),
                colorEntry.apply("#4169e1", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_GOLD.location(), 2, 3, 24.0, List.of(
                colorEntry.apply("gold", 0.15),
                colorEntry.apply("yellow", 0.15),
                colorEntry.apply("#ffdd55", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_EMERALD.location(), 3, 3, 16.0, List.of(
                colorEntry.apply("dark_green", 0.15),
                colorEntry.apply("green", 0.15),
                colorEntry.apply("#55ff88", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_DIAMOND.location(), 3, 3, 16.0, List.of(
                colorEntry.apply("#3fffdc", 0.15),
                colorEntry.apply("aqua", 0.15),
                colorEntry.apply("#3fc5ff", 0.15),
                colorEntry.apply("#8b7dff", 0.15),
                colorEntry.apply("#ff9cf0", 0.15)
        ), 1.0, VisionConfig.DisplayType.GLOW}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_EMERALD.location(), 4, -1, 16.0, List.of(
                colorEntry.apply("dark_green", 0.15),
                colorEntry.apply("green", 0.15),
                colorEntry.apply("#55ff88", 0.15)
        ), 1.0, VisionConfig.DisplayType.X_RAY_OUTLINE}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_DIAMOND.location(), 4, -1, 16.0, List.of(
                colorEntry.apply("#3fffdc", 1d),
                colorEntry.apply("aqua", 1d),
                colorEntry.apply("#3fc5ff", 1d),
                colorEntry.apply("#8b7dff", 1d),
                colorEntry.apply("#ff9cf0", 1d)
        ), 1.0, VisionConfig.DisplayType.X_RAY_OUTLINE}));

        list.add(entry.apply(new Object[]{"#" + Tags.Blocks.ORES_NETHERITE_SCRAP.location(), 4, -1, 16.0, List.of(
                colorEntry.apply("dark_gray", 1d),
                colorEntry.apply("#3b3b3b", 1d),
                colorEntry.apply("#6e4a3a", 1d)
        ), 1.0, VisionConfig.DisplayType.X_RAY_OUTLINE}));

        return list;
    }
}
