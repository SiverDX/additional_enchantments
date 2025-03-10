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
    private static Map<ResourceKey<Block>, VisionData> VISION_DATA = new HashMap<>();

    /** Used for the max. range */
    private static long lastUpdate;
    private static long lastReload;

    public static @Nullable VisionConfig.VisionData get(final Block block) {
        //noinspection deprecation -> ignore
        return VISION_DATA.get(block.builtInRegistryHolder().key());
    }

    public static double getMaxRange(final int enchantmentLevel) {
        if (lastUpdate < lastReload) {
            lastUpdate = System.currentTimeMillis();
            MAX_RANGE_CACHE.remove(enchantmentLevel);
        }

        return MAX_RANGE_CACHE.computeIfAbsent(enchantmentLevel, key -> {
            double currentRange = 0;

            for (VisionData data : VISION_DATA.values()) {
                if (enchantmentLevel >= data.requiredLevel()) {
                    double range = data.calculateRange(enchantmentLevel);

                    if (range > currentRange) {
                        currentRange = range;
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

        reload(player.level().registryAccess());
    }

    private static void reload(final RegistryAccess access) {
        if (!ServerConfig.SPEC.isLoaded()) {
            return;
        }

        Map<ResourceKey<Block>, VisionData> newEntries = new HashMap<>();

        RAW_ORE_VISION_ENTRIES.get().forEach(entry -> {
            VisionData data = VisionData.fromString(entry);

            if (data.resource().startsWith("#")) {
                ResourceLocation resource = new ResourceLocation(data.resource().substring(1));

                access.registryOrThrow(Registries.BLOCK).getTag(TagKey.create(Registries.BLOCK, resource)).ifPresent(tag -> {
                    //noinspection unchecked -> cast is valid
                    ((HolderSet$NamedAccess<Block>) tag).additional_enchantments$contents().forEach(block -> newEntries.put(block.unwrapKey().orElseThrow(), data));
                });
            } else {
                newEntries.put(ResourceKey.create(Registries.BLOCK, new ResourceLocation(data.resource())), data);
            }
        });

        VISION_DATA = newEntries;
        lastReload = System.currentTimeMillis();

        AE.LOG.debug("Reloaded vision entries: {}", VISION_DATA);
    }

    public enum Type {
        OUTLINE,
        PARTICLE
    }

    public record VisionData(String resource, int requiredLevel, double range, double rangePerLevel, int color) {
        private static final int RESOURCE = 0;
        private static final int REQUIRED_LEVEL = 1;
        private static final int BASE_RANGE = 2;
        private static final int RANGE_PER_LEVEL = 3;
        private static final int COLOR = 4;

        public double calculateRange(final int enchantmentLevel) {
            return range + rangePerLevel * enchantmentLevel;
        }

        public static VisionData fromString(final String data) {
            String[] entries = data.split(";");
            //noinspection DataFlowIssue -> color is present
            return new VisionData(
                    entries[RESOURCE],
                    Integer.parseInt(entries[REQUIRED_LEVEL]),
                    Double.parseDouble(entries[BASE_RANGE]),
                    Double.parseDouble(entries[RANGE_PER_LEVEL]),
                    TextColor.parseColor(entries[COLOR]).getValue()
            );
        }

        @SuppressWarnings("RedundantIfStatement") // ignore for clarity
        public static boolean validate(final String data) {
            try {
                VisionData vision = fromString(data);
                String resource;

                if (vision.resource().startsWith("#")) {
                    resource = vision.resource().substring(1);
                } else {
                    resource = vision.resource();
                }

                if (!ResourceLocation.isValidResourceLocation(resource)) {
                    return false;
                }

                if (vision.range() < 0 || vision.rangePerLevel() < 0) {
                    return false;
                }

                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}
