package de.cadentem.additional_enchantments.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.mixin.HolderSet$NamedAccess;
import de.cadentem.additional_enchantments.util.ColorUtils;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisionConfig {
    public static ForgeConfigSpec.ConfigValue<List<?>> RAW_ENTRIES;

    /** Max. range per enchantment level */
    private static final HashMap<Integer, Double> MAX_RANGE = new HashMap<>();
    private static Map<ResourceKey<Block>, List<VisionData>> DATA = new HashMap<>();
    private static Map<SpecialBlock, List<VisionData>> SPECIAL_BLOCK_DATA = new HashMap<>();

    /** Used for the max. range */
    private static long lastUpdate;
    private static long lastReload;

    public enum DisplayType {
        X_RAY_OUTLINE, GLOW, PARTICLES
    }

    public enum SpecialBlock {
        TREASURE("$treasure");

        private final String key;

        SpecialBlock(final String key) {
            this.key = key;
        }

        public static @Nullable SpecialBlock fromKey(final String key) {
            for (SpecialBlock value : values()) {
                if (value.getKey().equals(key)) {
                    return value;
                }
            }

            return null;
        }

        public @Nullable VisionData get(final int enchantmentLevel) {
            List<VisionData> entries = SPECIAL_BLOCK_DATA.get(this);

            if (entries == null) {
                return null;
            }

            for (VisionData entry : entries) {
                if (entry.levelBounds().matches(enchantmentLevel)) {
                    return entry;
                }
            }

            return null;
        }

        public String getKey() {
            return key;
        }
    }

    public static @Nullable VisionConfig.VisionData get(final Block block, final int enchantmentLevel) {
        //noinspection deprecation -> ignore
        List<VisionData> entries = DATA.get(block.builtInRegistryHolder().key());

        if (entries == null) {
            return null;
        }

        for (VisionData entry : entries) {
            if (entry.levelBounds().matches(enchantmentLevel)) {
                return entry;
            }
        }

        return null;
    }

    public static double getMaxRange(final int enchantmentLevel) {
        if (lastUpdate < lastReload) {
            lastUpdate = System.currentTimeMillis();
            // Make sure to remove the old entry from both
            MAX_RANGE.clear();
        }

        return MAX_RANGE.computeIfAbsent(enchantmentLevel, key -> {
            double currentRange = 0;

            for (List<VisionData> entries : DATA.values()) {
                for (VisionData entry : entries) {
                    if (entry.levelBounds().matches(enchantmentLevel) && entry.range() > currentRange) {
                        currentRange = entry.range();
                    }
                }
            }

            return currentRange;
        });
    }

    public static void updateFromReload(final TagsUpdatedEvent event) {
        reload(event.getRegistryAccess());
    }

    public static void updateFromConfig(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != ServerConfig.SPEC) {
            return;
        }

        Player player = ClientProxy.getLocalPlayer();

        if (player == null) {
            return;
        }

        reload(player.level.registryAccess());
    }

    private static void reload(final RegistryAccess access) {
        if (!ServerConfig.SPEC.isLoaded()) {
            return;
        }

        initializeData(access);
    }

    private static void initializeData(final RegistryAccess access) {
        Map<ResourceKey<Block>, List<VisionData>> newEntries = new HashMap<>();
        Map<SpecialBlock, List<VisionData>> newSpecialEntries = new HashMap<>();

        RAW_ENTRIES.get().forEach(entry -> {
            ParsedEntry parsed = ParsedEntry.from(entry);

            if (parsed.resource().startsWith("$")) {
                newSpecialEntries.computeIfAbsent(SpecialBlock.fromKey(parsed.resource()), key -> new ArrayList<>()).add(parsed.data());
            } else if (parsed.resource().startsWith("#")) {
                ResourceLocation resource = new ResourceLocation(parsed.resource().substring(1));

                access.registryOrThrow(Registry.BLOCK_REGISTRY).getTag(TagKey.create(Registry.BLOCK_REGISTRY, resource)).ifPresent(tag -> {
                    //noinspection unchecked -> cast is valid
                    ((HolderSet$NamedAccess<Block>) tag).additional_enchantments$contents().forEach(block -> {
                        newEntries.computeIfAbsent(block.unwrapKey().orElseThrow(), key -> new ArrayList<>()).add(parsed.data());
                    });
                });
            } else {
                newEntries.computeIfAbsent(ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(parsed.resource())), key -> new ArrayList<>()).add(parsed.data());
            }
        });

        DATA = newEntries;
        SPECIAL_BLOCK_DATA = newSpecialEntries;
        AE.LOG.debug("Reloaded treasure finder entries:\n - Normal blocks: {}\n - Special blocks: {}", DATA, SPECIAL_BLOCK_DATA);
        lastReload = System.currentTimeMillis();
    }

    public record VisionData(VisionConfig.DisplayType displayType, double range, List<Integer> colorsARGB, double colorShiftRate, MinMaxBounds.Ints levelBounds) {}

    public record ParsedEntry(String resource, int fromLevel, int toLevel, double range, List<Integer> colorsARGB, double colorShiftRate, VisionConfig.DisplayType displayType) {
        public VisionData data() {
            MinMaxBounds.Ints levelBounds;

            if (toLevel > 0) {
                levelBounds = MinMaxBounds.Ints.between(fromLevel, toLevel);
            } else {
                levelBounds = MinMaxBounds.Ints.atLeast(fromLevel);
            }

            return new VisionData(displayType, range, colorsARGB, colorShiftRate, levelBounds);
        }

        public static ParsedEntry fromConfig(final UnmodifiableConfig config) {
            String resource = config.get("resource");

            int fromLevel = config.getInt("from_level");
            int toLevel = config.getInt("to_level");

            double range = config.get("range");
            double colorShiftRate = config.get("color_shift_rate");

            DisplayType displayType;
            Object parsedDisplayType = config.get("display_type");

            if (parsedDisplayType instanceof DisplayType type) {
                displayType = type;
            } else if (parsedDisplayType instanceof String string) {
                displayType = DisplayType.valueOf(string.toUpperCase());
            } else {
                throw new IllegalArgumentException("Unsupported display type: " + parsedDisplayType.getClass());
            }

            List<Integer> colors = new ArrayList<>();

            if (config.get("colors") instanceof List<?> list) {
                for (Object entry : list) {
                    if (entry instanceof UnmodifiableConfig colorConfig) {
                        TextColor color = TextColor.parseColor(colorConfig.get("color"));

                        if (color == null) {
                            throw new IllegalArgumentException("Invalid color entry: " + colorConfig.get("color"));
                        }

                        int base = color.getValue();
                        double alpha = colorConfig.get("alpha");
                        colors.add(ColorUtils.withAlpha(base, (float) alpha));
                    } else {
                        throw new IllegalArgumentException("Unsupported color entry type: " + entry.getClass());
                    }
                }
            }

            return new ParsedEntry(resource, fromLevel, toLevel, range, colors, colorShiftRate, displayType);
        }

        public static ParsedEntry from(final Object value) {
            if (value instanceof UnmodifiableConfig config) {
                return fromConfig(config);
            }

            throw new IllegalArgumentException("Unsupported value type: " + value);
        }

        public static boolean validate(final Object data) {
            try {
                ParsedEntry vision = from(data);
                String resource;

                if (vision.resource().startsWith("$")) {
                    if (SpecialBlock.fromKey(vision.resource()) == null) {
                        AE.LOG.error("Invalid resource: {}", vision.resource());
                        return false;
                    }
                } else {
                    if (vision.resource().startsWith("#")) {
                        resource = vision.resource().substring(1);
                    } else {
                        resource = vision.resource();
                    }

                    if (!ResourceLocation.isValidResourceLocation(resource)) {
                        AE.LOG.error("Invalid resource: {}", vision.resource());
                        return false;
                    }
                }

                if (vision.range() < 0) {
                    AE.LOG.error("Invalid range: {}", vision.range());
                    return false;
                }

                if (vision.colorShiftRate() < 0) {
                    AE.LOG.error("Invalid color shift rate: {}", vision.colorShiftRate());
                    return false;
                }

                if (vision.fromLevel() < 0) {
                    AE.LOG.error("Invalid from level: {}", vision.fromLevel());
                    return false;
                }

                return true;
            } catch (Exception exception) {
                AE.LOG.error("Invalid vision entry: {}", data, exception);
                return false;
            }
        }
    }
}
