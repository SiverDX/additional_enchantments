package de.cadentem.additional_enchantments.config;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.mixin.HolderSet$NamedAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
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
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RAW_ORE_VISION_ENTRIES;

    private static final HashMap<Integer, Double> MAX_RANGE_CACHE = new HashMap<>();
    private static Map<Integer, Map<ResourceKey<Block>, VisionData>> VISION_DATA = new HashMap<>();

    /** Used for the max. range */
    private static long lastUpdate;
    private static long lastReload;

    public static @Nullable VisionConfig.VisionData get(final int enchantmentLevel, final Block block) {
        Map<ResourceKey<Block>, VisionData> blocks = VISION_DATA.get(enchantmentLevel);

        if (blocks == null) {
            return null;
        }

        //noinspection deprecation -> ignore
        return blocks.get(block.builtInRegistryHolder().key());
    }

    public static double getMaxRange(final int enchantmentLevel) {
        if (lastUpdate < lastReload) {
            lastUpdate = System.currentTimeMillis();
            MAX_RANGE_CACHE.remove(enchantmentLevel);
        }

        return MAX_RANGE_CACHE.computeIfAbsent(enchantmentLevel, key -> {
            double currentRange = 0;

            Map<ResourceKey<Block>, VisionData> blocks = VISION_DATA.get(enchantmentLevel);

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

        reload(player.level().registryAccess());
    }

    private static void reload(final RegistryAccess access) {
        if (!ServerConfig.SPEC.isLoaded()) {
            return;
        }

        Map<Integer, Map<ResourceKey<Block>, VisionData>> newEntries = new HashMap<>();

        RAW_ORE_VISION_ENTRIES.get().forEach(entry -> {
            ParsedEntry parsed = ParsedEntry.fromString(entry);

            if (parsed.resource().startsWith("#")) {
                ResourceLocation resource = new ResourceLocation(parsed.resource().substring(1));

                access.registryOrThrow(Registries.BLOCK).getTag(TagKey.create(Registries.BLOCK, resource)).ifPresent(tag -> {
                    //noinspection unchecked -> cast is valid
                    ((HolderSet$NamedAccess<Block>) tag).additional_enchantments$contents().forEach(block -> {
                        newEntries.computeIfAbsent(parsed.requiredLevel(), key -> new HashMap<>())
                                .put(block.unwrapKey().orElseThrow(), parsed.data());
                    });
                });
            } else {
                newEntries.computeIfAbsent(parsed.requiredLevel(), key -> new HashMap<>())
                        .put(ResourceKey.create(Registries.BLOCK, new ResourceLocation(parsed.resource())), parsed.data());
            }
        });

        VISION_DATA = newEntries;
        lastReload = System.currentTimeMillis();

        AE.LOG.debug("Reloaded vision entries: {}", VISION_DATA);
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
        private static final int COLOR = 4;

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
