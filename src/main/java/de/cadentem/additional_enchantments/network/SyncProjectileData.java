package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.client.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncProjectileData(CompoundTag tag, int entityId) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
        buffer.writeInt(entityId);
    }

    public static SyncProjectileData decode(final FriendlyByteBuf buffer) {
        return new SyncProjectileData(buffer.readNbt(), buffer.readInt());
    }

    public static void handle(final SyncProjectileData packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleSyncProjectileData(packet.tag, packet.entityId));
        }

        context.setPacketHandled(true);
    }
}
