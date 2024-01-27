package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncConfiguration(CompoundTag tag) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static SyncConfiguration decode(final FriendlyByteBuf buffer) {
        return new SyncConfiguration(buffer.readNbt());
    }

    public static void handle(final SyncConfiguration packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> ConfigurationProvider.getCapability(context.getSender()).ifPresent(configuration -> configuration.deserializeNBT(packet.tag)));
        } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleSyncConfiguration(packet.tag));
        }

        context.setPacketHandled(true);
    }
}
