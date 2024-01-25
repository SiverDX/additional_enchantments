package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.ExplosiveTipEnchantment;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import de.cadentem.additional_enchantments.enchantments.StraightShotEnchantment;
import de.cadentem.additional_enchantments.enchantments.TippedEnchantment;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.network.SyncConfiguration;
import de.cadentem.additional_enchantments.network.SyncProjectileData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CapabilityHandler {
    public static final Capability<Configuration> CONFIGURATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ProjectileData> PROJECTILE_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static ResourceLocation CONFIGURATION = new ResourceLocation(AE.MODID, "configuration");
    public static ResourceLocation PROJECTILE_DATA = new ResourceLocation(AE.MODID, "projectile_data"); // TODO :: Sync in PlayerEvent.StartTracking needed?

    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (player instanceof FakePlayer) {
                return;
            }

            event.addCapability(CONFIGURATION, new ConfigurationProvider());
        } else if (event.getObject() instanceof Projectile) {
            event.addCapability(PROJECTILE_DATA, new ProjectileDataProvider());
        }
    }

    @SubscribeEvent
    public static void handleProjectile(final EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            TippedEnchantment.applyEffects(projectile);
            StraightShotEnchantment.updateGravity(projectile);
            HomingEnchantment.setEnchantmentLevel(projectile);
            ExplosiveTipEnchantment.setEnchantmentLevel(projectile);
            syncProjectileData(projectile);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleProjectileImpact(final ProjectileImpactEvent event) {
        if (!event.isCanceled()) {
            ProjectileDataProvider.getCapability(event.getProjectile()).ifPresent(data -> data.handleImpact(event.getProjectile()));
        }
    }

    @SubscribeEvent // Only called server-side
    public static void handlePlayerDeath(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();

            ConfigurationProvider.getCapability(event.getEntity()).ifPresent(configuration -> {
                ConfigurationProvider.getCapability(event.getOriginal()).ifPresent(oldConfiguration -> {
                    configuration.deserializeNBT(oldConfiguration.serializeNBT());
                    syncConfiguration(event.getEntity());
                });
            });

            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent // Only called server-side
    public static void handlePlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        syncConfiguration(event.getEntity());
    }

    @SubscribeEvent // Only called server-side
    public static void handleDimensionChange(final PlayerEvent.PlayerChangedDimensionEvent event) {
        syncConfiguration(event.getEntity());
    }

    public static void syncProjectileData(final Projectile projectile) {
        if (projectile.getLevel().isClientSide()) {
            // There should be no client-triggered changes
            return;
        }

        ProjectileDataProvider.getCapability(projectile).ifPresent(projectileData -> syncProjectileData(projectile, projectileData.serializeNBT()));
    }

    public static void syncProjectileData(final Projectile projectile, final CompoundTag tag) {
        if (projectile.getLevel().isClientSide()) {
            // There should be no client-triggered changes
            return;
        }

        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> projectile), new SyncProjectileData(tag, projectile.getId()));
    }

    public static void syncConfiguration(final Player player) {
        ConfigurationProvider.getCapability(player).ifPresent(configuration -> syncConfiguration(player, configuration.serializeNBT()));
    }

    public static void syncConfiguration(final Player player, final CompoundTag tag) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncConfiguration(tag));
        } else {
            NetworkHandler.CHANNEL.sendToServer(new SyncConfiguration(tag));
        }
    }
}
