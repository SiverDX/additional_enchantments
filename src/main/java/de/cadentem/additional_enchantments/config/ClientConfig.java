package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.OreSightHandler;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue CACHE_EXPIRE;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_SIGHT_CONFIGS_INTERNAL;
    private static @Nullable List<OreSightConfig> ORE_SIGHT_CONFIGS;

    public record OreSightConfig(@Nullable TagKey<Block> tag, @Nullable Block block, Vec3i color) {
        public boolean test(final BlockState state) {
            if (block != null && state.is(block)) {
                return true;
            }

            return tag != null && state.is(tag);
        }

        @SuppressWarnings("ConstantConditions")
        public static @Nullable OreSightConfig fromString(final @NotNull String data) {
            OreSightConfig config;

            String[] split = data.split(";");
            String rawLocation = split[0];
            Vec3i color = new Vec3i(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));

            if (rawLocation.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, ResourceLocation.tryParse(rawLocation.substring(1)));

                if (!ForgeRegistries.BLOCKS.tags().isKnownTagName(tag)) {
                    return null;
                }

                config = new OreSightConfig(tag, null, color);
            } else {
                Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(rawLocation));

                if (block == Blocks.AIR) {
                    return null;
                }

                config = new OreSightConfig(null, block, color);
            }

            return config;
        }
    }

    static {
        BUILDER.push("ore_sight");
        CACHE_EXPIRE = BUILDER.comment("Determines after how many seconds cached entries expire (affects how fast blocks update their outline) (0 disables caching - not recommended)").defineInRange("cache_expire", 2, 0, 10);

        List<String> defaultConfig = List.of(
                "#" + AEBlockTags.COMMON_ORE.location() + ";165;42;42",
                "#" + AEBlockTags.UNCOMMON_ORE.location() + ";255;215;0",
                "#" + AEBlockTags.RARE_ORE.location() + ";64;224;208",
                "#" + Tags.Blocks.ORES.location() + ";255;255;255"
        );

        ORE_SIGHT_CONFIGS_INTERNAL = BUILDER.comment("Color configuration for ore sight - syntax: [<block_tag>;<red>;<green>;<blue>], e.g. [#forge:ores/gold;255;215;0] or [minecraft:ancient_debris;160,32,240]").defineList("ore_sight_configs", defaultConfig, ClientConfig::validateOreSightConfig);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void reloadConfigFromTags(final TagsUpdatedEvent ignored) {
        reloadConfig();
    }

    public static void reloadConfigFromEdit(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            reloadConfig();
        }
    }

    public static void reloadConfig() {
        if (ORE_SIGHT_CONFIGS_INTERNAL.get().isEmpty()) {
            return;
        }

        List<OreSightConfig> newConfigs = new ArrayList<>();
        ORE_SIGHT_CONFIGS_INTERNAL.get().forEach(data -> {
            OreSightConfig config = OreSightConfig.fromString(data);

            if (config == null) {
                AE.LOG.warn("Ore Sight config is invalid likely due to non-existent block or tag [{}]", data);
            } else {
                newConfigs.add(config);
            }
        });

        ORE_SIGHT_CONFIGS = newConfigs;
        AE.LOG.info("Reloaded Ore Sight configuration: [{}]", ORE_SIGHT_CONFIGS);
    }

    public static @NotNull Vec3i getColor(final BlockState state) {
        if (ORE_SIGHT_CONFIGS == null) {
            return OreSightHandler.NO_COLOR;
        }

        for (OreSightConfig config : ORE_SIGHT_CONFIGS) {
            if (config.test(state)) {
                return config.color();
            }
        }

        return OreSightHandler.NO_COLOR;
    }

    private static boolean validateOreSightConfig(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length == 4) {
                if (!ResourceLocation.isValidResourceLocation(data[0])) {
                    return false;
                }

                if (isInvalidColor(data[1])) {
                    return false;
                }

                if (isInvalidColor(data[2])) {
                    return false;
                }

                return !isInvalidColor(data[3]);
            }
        }

        return false;
    }

    private static boolean isInvalidColor(final String raw) {
        try {
            int color = Integer.parseInt(raw);
            return color < 0 || color > 255;
        } catch (NumberFormatException ignored) {
            return true;
        }
    }
}
