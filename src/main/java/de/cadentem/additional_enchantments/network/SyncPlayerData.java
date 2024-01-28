package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncPlayerData(CompoundTag tag) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static SyncPlayerData decode(final FriendlyByteBuf buffer) {
        return new SyncPlayerData(buffer.readNbt());
    }

    public static void handle(final SyncPlayerData packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> PlayerDataProvider.getCapability(context.getSender()).ifPresent(data -> data.deserializeNBT(packet.tag)));
        } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleSyncPlayerData(packet.tag));
        }

        context.setPacketHandled(true);
    }
}
