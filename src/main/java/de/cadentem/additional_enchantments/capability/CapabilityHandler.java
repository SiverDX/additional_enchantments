package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.network.SyncCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CapabilityHandler {
    public static final Capability<Configuration> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static ResourceLocation capabilityResource = new ResourceLocation(AE.MODID, "configuration");

    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (player instanceof FakePlayer) {
                return;
            }

            event.addCapability(capabilityResource, new CapabilityProvider());
        }
    }

    @SubscribeEvent
    public static void handlePlayerDeath(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();

            CapabilityProvider.getCapability(event.getEntity()).ifPresent(configuration -> {
                CapabilityProvider.getCapability(event.getOriginal()).ifPresent(oldConfiguration -> {
                    configuration.deserializeNBT(oldConfiguration.serializeNBT());
                    syncCapability(event.getEntity());
                });
            });

            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void handlePlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        syncCapability(event.getEntity());
    }

    @SubscribeEvent
    public static void handleDimensionChange(final PlayerEvent.PlayerChangedDimensionEvent event) {
        syncCapability(event.getEntity());
    }

    public static void syncCapability(final Entity entity) {
        if (entity instanceof ServerPlayer) {
            CapabilityProvider.getCapability(entity).ifPresent(configuration -> syncCapability(entity, configuration.serializeNBT()));
        } else if (entity instanceof Player && entity.level().isClientSide()) {
            CapabilityProvider.getCapability(entity).ifPresent(configuration -> syncCapability(entity, configuration.serializeNBT()));
        }
    }

    public static void syncCapability(final Entity entity, final CompoundTag tag) {
        if (entity instanceof ServerPlayer serverPlayer) {
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapability(tag));
        } else if (entity instanceof Player && entity.level().isClientSide()) {
            NetworkHandler.CHANNEL.sendToServer(new SyncCapability(tag));
        }
    }
}
