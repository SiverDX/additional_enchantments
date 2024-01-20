package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCapability {
    public CompoundTag tag;

    public SyncCapability(final CompoundTag tag) {
        this.tag = tag;
    }

    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeNbt(tag);
    }

    public static SyncCapability decode(final FriendlyByteBuf buffer) {
        return new SyncCapability(buffer.readNbt());
    }

    public static void handle(final SyncCapability packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> CapabilityProvider.getCapability(context.getSender()).ifPresent(configuration -> configuration.deserializeNBT(packet.tag)));
        } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleSyncCapability(packet.tag));
        }

        context.setPacketHandled(true);
    }
}
