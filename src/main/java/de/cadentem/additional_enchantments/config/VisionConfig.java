package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.mixin.HolderSet$NamedAccess;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisionConfig {
    // Ore Sight
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RAW_ORE_SIGHT_ENTRIES;
    private static final HashMap<Integer, Double> MAX_ORE_SIGHT_RANGE = new HashMap<>();
    private static Map<Integer, Map<ResourceKey<Block>, VisionData>> ORE_SIGHT_DATA = new HashMap<>();

    // Treasure Finder
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RAW_TREASURE_FINDER_ENTRIES;
    private static final HashMap<Integer, Double> MAX_TREASURE_FINDER_RANGE = new HashMap<>();
    private static Map<Integer, Map<ResourceKey<Block>, VisionData>> TREASURE_FINDER_DATA = new HashMap<>();

    /** Used for the max. range */
    private static long lastUpdate;
    private static long lastReload;

    public static @Nullable VisionConfig.VisionData get(final Type type, final int enchantmentLevel, final Block block) {
        Map<ResourceKey<Block>, VisionData> blocks = switch (type) {
            case ORE_SIGHT -> ORE_SIGHT_DATA.get(enchantmentLevel);
            case TREASURE_FINDER -> TREASURE_FINDER_DATA.get(enchantmentLevel);
        };

        if (blocks == null) {
            return null;
        }

        //noinspection deprecation -> ignore
        return blocks.get(block.builtInRegistryHolder().key());
    }

    public static double getMaxRange(final Type type, final int enchantmentLevel) {
        HashMap<Integer, Double> ranges = switch (type) {
            case ORE_SIGHT -> MAX_ORE_SIGHT_RANGE;
            case TREASURE_FINDER -> MAX_TREASURE_FINDER_RANGE;
        };

        if (lastUpdate < lastReload) {
            lastUpdate = System.currentTimeMillis();
            // Make sure to remove the old entry from both
            MAX_ORE_SIGHT_RANGE.clear();
            MAX_TREASURE_FINDER_RANGE.clear();
        }

        return ranges.computeIfAbsent(enchantmentLevel, key -> {
            double currentRange = 0;

            Map<ResourceKey<Block>, VisionData> blocks = switch (type) {
                case ORE_SIGHT -> ORE_SIGHT_DATA.get(enchantmentLevel);
                case TREASURE_FINDER -> TREASURE_FINDER_DATA.get(enchantmentLevel);
            };

            if (blocks == null) {
                return currentRange;
            }

            for (VisionData data : blocks.values()) {
                if (data.range() > currentRange) {
                    currentRange = data.range();
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

        initializeData(access, Type.ORE_SIGHT);
        initializeData(access, Type.TREASURE_FINDER);
    }

    private static void initializeData(final RegistryAccess access, final Type type) {
        Map<Integer, Map<ResourceKey<Block>, VisionData>> newEntries = new HashMap<>();

        List<? extends String> entries = switch (type) {
            case ORE_SIGHT -> RAW_ORE_SIGHT_ENTRIES.get();
            case TREASURE_FINDER -> RAW_TREASURE_FINDER_ENTRIES.get();
        };

        entries.forEach(entry -> {
            ParsedEntry parsed = ParsedEntry.fromString(entry);

            if (parsed.resource().startsWith("#")) {
                ResourceLocation resource = new ResourceLocation(parsed.resource().substring(1));

                access.registryOrThrow(Registry.BLOCK_REGISTRY).getTag(TagKey.create(Registry.BLOCK_REGISTRY, resource)).ifPresent(tag -> {
                    //noinspection unchecked -> cast is valid
                    ((HolderSet$NamedAccess<Block>) tag).additional_enchantments$contents().forEach(block -> {
                        newEntries.computeIfAbsent(parsed.requiredLevel(), key -> new HashMap<>())
                                .put(block.unwrapKey().orElseThrow(), parsed.data());
                    });
                });
            } else {
                newEntries.computeIfAbsent(parsed.requiredLevel(), key -> new HashMap<>())
                        .put(ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(parsed.resource())), parsed.data());
            }
        });

        switch (type) {
            case ORE_SIGHT -> {
                ORE_SIGHT_DATA = newEntries;
                AE.LOG.debug("Reloaded ore sight entries: {}", ORE_SIGHT_DATA);
            }
            case TREASURE_FINDER -> {
                TREASURE_FINDER_DATA = newEntries;
                AE.LOG.debug("Reloaded treasure finder entries: {}", TREASURE_FINDER_DATA);
            }
        }

        lastReload = System.currentTimeMillis();
    }

    public enum Type {
        ORE_SIGHT,
        TREASURE_FINDER
    }

    public record VisionData(double range, int color) {}

    public record ParsedEntry(String resource, int requiredLevel, double range, int color) {
        private static final int RESOURCE = 0;
        private static final int REQUIRED_LEVEL = 1;
        private static final int RANGE = 2;
        private static final int COLOR = 3;

        public VisionData data() {
            return new VisionData(range, color);
        }

        public static ParsedEntry fromString(final String data) {
            String[] entries = data.split(";");
            //noinspection DataFlowIssue -> color is present
            return new ParsedEntry(
                    entries[RESOURCE],
                    Integer.parseInt(entries[REQUIRED_LEVEL]),
                    Double.parseDouble(entries[RANGE]),
                    TextColor.parseColor(entries[COLOR]).getValue()
            );
        }

        @SuppressWarnings("RedundantIfStatement") // ignore for clarity
        public static boolean validate(final String data) {
            try {
                ParsedEntry vision = fromString(data);
                String resource;

                if (vision.resource().startsWith("#")) {
                    resource = vision.resource().substring(1);
                } else {
                    resource = vision.resource();
                }

                if (!ResourceLocation.isValidResourceLocation(resource)) {
                    return false;
                }

                if (vision.range() < 0) {
                    return false;
                }

                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}
