package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(AE.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, SyncCapability.class, SyncCapability::encode, SyncCapability::decode, SyncCapability::handle);
        CHANNEL.registerMessage(1, SyncHomingData.class, SyncHomingData::encode, SyncHomingData::decode, SyncHomingData::handle);
    }
}
