package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.capability.PlayerData;
import de.cadentem.additional_enchantments.client.OreSightHandler;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
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
import java.util.Comparator;
import java.util.List;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue CACHE_EXPIRE;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_SIGHT_CONFIGS_INTERNAL;
    private static @Nullable List<OreSightConfig> ORE_SIGHT_CONFIGS;

    public static class OreSightConfig {
        public final Vec3i color;
        public final int rarity;

        private final TagKey<Block> tag;
        private final Block block;

        public OreSightConfig(int rarity, final Vec3i color, final TagKey<Block> tag) {
            this.rarity = rarity;
            this.color = color;
            this.tag = tag;
            this.block = null;
        }

        public OreSightConfig(int rarity, final Vec3i color, final Block block) {
            this.rarity = rarity;
            this.color = color;
            this.tag = null;
            this.block = block;
        }

        public boolean test(final BlockState state) {
            if (block != null && state.is(block)) {
                return true;
            }

            return tag != null && state.is(tag);
        }

        @SuppressWarnings("ConstantConditions")
        public static @Nullable OreSightConfig fromString(final @NotNull String data) {
            String[] split = data.split(";");
            int rarity = Integer.parseInt(split[0]);
            boolean isTag = split[1].startsWith("#");
            ResourceLocation location = ResourceLocation.tryParse(isTag ? split[1].substring(1) : split[1]);
            Vec3i color = new Vec3i(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));

            OreSightConfig config;

            if (isTag) {
                TagKey<Block> tag = TagKey.create(Registries.BLOCK, location);

                if (!ForgeRegistries.BLOCKS.tags().isKnownTagName(tag)) {
                    return null;
                }

                config = new OreSightConfig(rarity, color, tag);
            } else {
                Block block = ForgeRegistries.BLOCKS.getValue(location);

                if (block == Blocks.AIR) {
                    return null;
                }

                config = new OreSightConfig(rarity, color, block);
            }

            return config;
        }

        @Override
        public String toString() {
            return "OreSightConfig{" +
                    "color=" + color +
                    ", rarity=" + rarity +
                    ", tag=" + tag +
                    ", block=" + block +
                    '}';
        }
    }

    static {
        BUILDER.push("ore_sight");
        CACHE_EXPIRE = BUILDER.comment("Determines after how many seconds cached entries expire (affects how fast blocks update their outline) (0 disables caching - not recommended)").defineInRange("cache_expire", 2, 0, 10);

        List<String> defaultConfig = List.of(
                "0;#" + Tags.Blocks.ORES.location() + ";255;255;255",
                "1;#" + AEBlockTags.COMMON_ORE.location() + ";165;42;42",
                "2;#" + AEBlockTags.UNCOMMON_ORE.location() + ";255;215;0",
                "3;#" + AEBlockTags.RARE_ORE.location() + ";64;224;208"
        );

        ORE_SIGHT_CONFIGS_INTERNAL = BUILDER.comment("Color configuration for ore sight - syntax: [<rarity>;<block>;<red>;<green>;<blue>], e.g. [1;#forge:ores/gold;255;215;0] or [7;minecraft:ancient_debris;160,32,240]").defineList("ore_sight_configs", defaultConfig, ClientConfig::validateOreSightConfig);
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
                newConfigs.add(config); // TODO :: check if said rarity was already added?
            }
        });

        // Highest rarity will get tested first
        newConfigs.sort(Comparator.comparingInt(config -> ((OreSightConfig) config).rarity).reversed());

        ORE_SIGHT_CONFIGS = newConfigs;
        AE.LOG.info("Reloaded Ore Sight configuration: [{}]", ORE_SIGHT_CONFIGS);
    }

    public static int getNextRarity(int current) {
        if (ORE_SIGHT_CONFIGS == null || ORE_SIGHT_CONFIGS.isEmpty()) {
            return PlayerData.DISPLAY_NONE;
        }

        for (int i = ORE_SIGHT_CONFIGS.size() - 1; i >= 0; i--) {
            OreSightConfig config = ORE_SIGHT_CONFIGS.get(i);

            if (config.rarity > current) {
                return config.rarity;
            }
        }

        return PlayerData.DISPLAY_NONE;
    }

    public static @NotNull Vec3i getColor(final BlockState state, int displayRarity) {
        if (ORE_SIGHT_CONFIGS == null) {
            return OreSightHandler.NO_COLOR;
        }

        for (OreSightConfig config : ORE_SIGHT_CONFIGS) {
            if (config.rarity >= displayRarity && config.test(state)) {
                return config.color;
            }
        }

        return OreSightHandler.NO_COLOR;
    }

    private static boolean validateOreSightConfig(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length == 5) {
                try {
                    if (Integer.parseInt(data[0]) < 0) {
                        return false;
                    }
                } catch (NumberFormatException ignored) {
                    return false;
                }

                if (!ResourceLocation.isValidResourceLocation(data[1].startsWith("#") ? data[1].substring(1) : data[1])) {
                    return false;
                }

                if (isInvalidColor(data[2])) {
                    return false;
                }

                if (isInvalidColor(data[3])) {
                    return false;
                }

                if (isInvalidColor(data[4])) {
                    return false;
                }

                return true;
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
