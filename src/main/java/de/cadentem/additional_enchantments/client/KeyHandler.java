package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.capability.CapabilityHandler;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.enchantments.OreSightEnchantment;
import de.cadentem.additional_enchantments.enchantments.PerceptionEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class KeyHandler {
    public static KeyMapping CYCLE_TIPPED;
    public static KeyMapping CYCLE_HOMING;
    public static KeyMapping CYCLE_EXPLOSIVE_TIP;
    public static KeyMapping CYCLE_PERCEPTION;
    public static KeyMapping CYCLE_ORE_SIGHT;
    public static KeyMapping CYCLE_VOIDING;

    private static int LAST_PRESS_TICK;

    @SubscribeEvent
    public static void handleKey(final InputEvent.Key event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null || Minecraft.getInstance().screen != null || Math.abs(localPlayer.tickCount - LAST_PRESS_TICK) <= 20) {
            return;
        }

        AtomicBoolean playerDataChanged = new AtomicBoolean(false);

        if (event.getKey() == CYCLE_TIPPED.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.TIPPED.get()) > 0) {
                PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                    data.cycleEffectFilter();
                    localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Tipped", data.effectFilter.name()));
                    playerDataChanged.set(true);
                });
            }
        }

        if (event.getKey() == CYCLE_EXPLOSIVE_TIP.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.EXPLOSIVE_TIP.get()) > 0) {
                PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                    data.cycleExplosionType();
                    localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Explosive Tip", data.explosionType.name()));
                    playerDataChanged.set(true);
                });
            }
        }

        if (event.getKey() == CYCLE_HOMING.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.HOMING.get()) > 0) {
                if (localPlayer.isShiftKeyDown()) {
                    PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                        data.cycleHomingPriority();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Homing (priority)", data.homingPriority.name()));
                        playerDataChanged.set(true);
                    });
                } else {
                    PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                        data.cycleHomingFilter();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Homing (type)", data.homingTypeFilter.name()));
                        playerDataChanged.set(true);
                    });
                }
            }
        }

        if (event.getKey() == CYCLE_PERCEPTION.getKey().getValue()) {
            if (PerceptionEnchantment.getClientEnchantmentLevel() > 0) {
                if (localPlayer.isShiftKeyDown()) {
                    PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                        data.cycleItemFilter();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Perception (item filter)", data.itemFilter.name()));
                        playerDataChanged.set(true);
                    });
                } else {
                    PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                        data.cycleDisplayType();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Perception (display type)", data.displayType.name()));
                        playerDataChanged.set(true);
                    });
                }
            }
        }

        if (event.getKey() == CYCLE_ORE_SIGHT.getKey().getValue()) {
            if (OreSightEnchantment.getClientEnchantmentLevel() > 0) {
                PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                    data.cycleOreRarity();
                    localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Ore Sight", data.displayRarity));
                    playerDataChanged.set(true);
                    OreSightHandler.clearCache();
                });
            }
        }

        if (event.getKey() == CYCLE_VOIDING.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.VOIDING.get()) > 0) {
                PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
                    data.cycleVoiding();
                    localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Voiding", data.voidingState.name()));
                    playerDataChanged.set(true);
                });
            }
        }

        if (playerDataChanged.get()) {
            LAST_PRESS_TICK = localPlayer.tickCount;
            CapabilityHandler.syncPlayerData(localPlayer);
        }
    }
}
