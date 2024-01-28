package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(AE.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, SyncPlayerData.class, SyncPlayerData::encode, SyncPlayerData::decode, SyncPlayerData::handle);
        CHANNEL.registerMessage(1, SyncProjectileData.class, SyncProjectileData::encode, SyncProjectileData::decode, SyncProjectileData::handle);
    }
}
