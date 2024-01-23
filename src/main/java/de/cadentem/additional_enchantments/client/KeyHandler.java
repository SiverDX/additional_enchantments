package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.capability.CapabilityHandler;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
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

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KeyHandler {
    public static KeyMapping CYCLE_TIPPED;
    public static KeyMapping CYCLE_HOMING;
    public static KeyMapping CYCLE_EXPLOSIVE_TIP;
    public static KeyMapping CYCLE_PERCEPTION;

    @SubscribeEvent
    public static void handleKey(final InputEvent.Key event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AtomicBoolean changedConfiguration = new AtomicBoolean(false);

        if (event.getKey() == CYCLE_TIPPED.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.TIPPED.get()) > 0) {
                if (CYCLE_TIPPED.consumeClick()) {
                    ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                        configuration.cycleEffectFilter();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Tipped", configuration.effectFilter.name()));
                        changedConfiguration.set(true);
                    });
                }
            }
        } else if (event.getKey() == CYCLE_EXPLOSIVE_TIP.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.EXPLOSIVE_TIP.get()) > 0) {
                if (CYCLE_EXPLOSIVE_TIP.consumeClick()) {
                    ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                        configuration.cycleExplosionType();
                        localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Explosive Tip", configuration.explosionType.name()));
                        changedConfiguration.set(true);
                    });
                }
            }
        } else if (event.getKey() == CYCLE_HOMING.getKey().getValue()) {
            if (localPlayer.getMainHandItem().getEnchantmentLevel(AEEnchantments.HOMING.get()) > 0) {
                if (CYCLE_HOMING.consumeClick()) {
                    if (localPlayer.isShiftKeyDown()) {
                        ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                            configuration.cycleHomingPriority();
                            localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Homing (priority)", configuration.homingPriority.name()));
                            changedConfiguration.set(true);
                        });
                    } else {
                        ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                            configuration.cycleHomingFilter();
                            localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Homing (type)", configuration.homingTypeFilter.name()));
                            changedConfiguration.set(true);
                        });
                    }
                }
            }
        } else if (event.getKey() == CYCLE_PERCEPTION.getKey().getValue()) {
            if (PerceptionEnchantment.getClientEnchantmentLevel() > 0) {
                if (CYCLE_PERCEPTION.consumeClick()) {
                    if (localPlayer.isShiftKeyDown()) {
                        ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                            configuration.cycleItemFilter();
                            localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Perception (item filter)", configuration.itemFilter.name()));
                            changedConfiguration.set(true);
                        });
                    } else {
                        ConfigurationProvider.getCapability(localPlayer).ifPresent(configuration -> {
                            configuration.cycleDisplayType();
                            localPlayer.sendSystemMessage(Component.translatable("message.additional_enchantments.cycled_configuration", "Perception (display type)", configuration.displayType.name()));
                            changedConfiguration.set(true);
                        });
                    }
                }
            }
        }

        if (changedConfiguration.get()) {
            CapabilityHandler.syncConfiguration(localPlayer);
        }
    }
}
