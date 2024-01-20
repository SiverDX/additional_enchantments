package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import de.cadentem.additional_enchantments.core.ProjectileAccess;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ClientProxy {
    public static void handleSyncHomingData(int projectileId, int targetId, int enchantmentLevel) {
        Entity projectile = Minecraft.getInstance().level.getEntity(projectileId);

        if (projectile instanceof ProjectileAccess contextAccess) {
            if (targetId == -1) {
                contextAccess.additional_enchantments$getHomingContext().target = null;
                return;
            }

            Entity target = Minecraft.getInstance().level.getEntity(targetId);

            if (target instanceof LivingEntity livingTarget) {
                contextAccess.additional_enchantments$setHomingContext(new HomingEnchantment.HomingContext(livingTarget, enchantmentLevel));
            } else {
                AE.LOGGER.warn("Homing projectile target [{}] was not successfully synced to the client", target);
            }
        } else {
            AE.LOGGER.warn("Homing projectile [{}] was not successfully synced to the client", projectile);
        }
    }

    public static void handleSyncCapability(final CompoundTag tag) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        CapabilityProvider.getCapability(localPlayer).ifPresent(configuration -> {
            configuration.deserializeNBT(tag);
        });
    }
}
