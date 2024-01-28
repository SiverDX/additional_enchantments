package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class ClientProxy {
    public static void handleSyncPlayerData(final CompoundTag tag) {
        PlayerDataProvider.getCapability(Minecraft.getInstance().player).ifPresent(data -> data.deserializeNBT(tag));
    }

    public static void handleSyncProjectileData(final CompoundTag tag, int entityId) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            ProjectileDataProvider.getCapability(localPlayer.level().getEntity(entityId)).ifPresent(projectileData -> projectileData.deserializeNBT(tag));
        }
    }

    public static @Nullable Player getLocalPlayer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return Minecraft.getInstance().player;
        }

        return null;
    }
}
