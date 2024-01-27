package de.cadentem.additional_enchantments.enchantments;

import com.mojang.datafixers.util.Pair;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.core.interfaces.LivingEntityAccess;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class HunterEnchantment extends ConfigurableEnchantment {
    private static final Map<String, Pair<Integer, Integer>> CLIENT_CACHE = new HashMap<>();

    public HunterEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET, AEEnchantments.HUNTER_ID);
    }

    @SubscribeEvent
    public static void applyEffect(final LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.getLevel().isClientSide()) {
            return;
        }

        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), livingEntity);

        if (livingEntity instanceof Mob mob && mob.getTarget() instanceof Player player) {
            // Reset aggro when maximum stacks are reached
            ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                if (configuration.hasMaxHunterStacks(enchantmentLevel)) {
                    mob.setLastHurtByPlayer(null);
                    mob.setTarget(null);
                }
            });
        }

        if (livingEntity instanceof LivingEntityAccess access) {
            AtomicBoolean invisibilityValid = new AtomicBoolean(false);

            if (enchantmentLevel > 0) {
                if (/* Basically inside the block */ isBlockHunterRelevant(livingEntity.getFeetBlockState()) || /* Below feet */ isBlockHunterRelevant(livingEntity.getBlockStateOn())) {
                    if (livingEntity instanceof Player) {
                        ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> {
                            configuration.increaseHunterStacks(enchantmentLevel);
                            configuration.isOnHunterBlock = true;
                        });
                    }

                    invisibilityValid.set(true);

                    if (!livingEntity.isInvisible()) {
                        livingEntity.setInvisible(true);
                        access.additional_enchantments$setWasInvisibilityModified(true);
                    }
                } else {
                    if (livingEntity instanceof Player) {
                        ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> {
                            configuration.reduceHunterStacks(livingEntity, enchantmentLevel);

                            if (configuration.hunterStacks > 0) {
                                invisibilityValid.set(true);
                            }

                            configuration.isOnHunterBlock = false;
                        });
                    }
                }
            } else if (livingEntity instanceof Player) {
                ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> configuration.hunterStacks = 0);
            }

            if (!invisibilityValid.get()) {
                // Reset visibility in case it was modified by the hunter enchantment
                if (access.additional_enchantments$wasInvisibilityModified() && !livingEntity.hasEffect(MobEffects.INVISIBILITY)) {
                    livingEntity.setInvisible(false);
                    access.additional_enchantments$setWasInvisibilityModified(false);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void avoidTarget(final LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player player) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), player);

            if (enchantmentLevel > 0) {
                ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                    if (configuration.hasMaxHunterStacks(enchantmentLevel)) {
                        event.setNewTarget(null);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void causeCrit(final CriticalHitEvent event) {
        Player player = event.getEntity();

        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), player);

        if (enchantmentLevel > 0) {
            ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                if (configuration.hasMaxHunterStacks(enchantmentLevel)) {
                    event.setDamageModifier(event.getDamageModifier() + enchantmentLevel / 2f);
                    event.setResult(Event.Result.ALLOW);

                    configuration.hunterStacks = 0;
                }
            });
        }
    }

    @SubscribeEvent
    public static void modifyVisibility(final LivingEvent.LivingVisibilityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getLevel().isClientSide()) {
                return;
            }

            if (!player.isInvisible()) {
                return;
            }

            // Reduce invisibility effect if player is no longer on hunter-relevant blocks
            ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                if (!configuration.isOnHunterBlock && !player.hasEffect(MobEffects.INVISIBILITY)) {
                    int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), player);

                    if (enchantmentLevel > 0) {
                        event.modifyVisibility(event.getVisibilityModifier() + 1 - ((double) configuration.hunterStacks / HunterEnchantment.getMaxStacks(enchantmentLevel)));
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void removeCacheEntry(final EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            if (!entity.getLevel().isClientSide()) {
                return;
            }

            CLIENT_CACHE.remove(entity.getStringUUID());
        }
    }

    public static int getClientEnchantmentLevel() {
        return getClientEnchantmentLevel(ClientProxy.getLocalPlayer());
    }

    public static int getClientEnchantmentLevel(final Player player) {
        if (player == null) {
            return 0;
        }

        Pair<Integer, Integer> data = CLIENT_CACHE.get(player.getStringUUID());

        // Cache is being kept even when the player leaves
        if (data == null || Mth.abs(player.tickCount - data.getFirst()) > 20) {
            data = Pair.of(player.tickCount, EnchantmentHelper.getEnchantmentLevel(AEEnchantments.HUNTER.get(), player));
            CLIENT_CACHE.put(player.getStringUUID(), data);
        }

        return data.getSecond();
    }

    public static int getMaxStacks(int enchantmentLevel) {
        return 20 * (3 + Math.max(0, AEEnchantments.HUNTER.get().getMaxLevel() - enchantmentLevel));
    }

    public static boolean isBlockHunterRelevant(final BlockState state) {
        Material material = state.getMaterial();

        if (material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
            return true;
        }

        return state.is(AEBlockTags.HUNTER_RELEVANT);
    }
}
