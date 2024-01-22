package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHomingData {
    public int projectileId;
    public final int targetId;
    public final int enchantmentLevel;

    public SyncHomingData(int projectileId, int targetId, int enchantmentLevel) {
        this.projectileId = projectileId;
        this.targetId = targetId;
        this.enchantmentLevel = enchantmentLevel;
    }

    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeInt(projectileId);
        buffer.writeInt(targetId);
        buffer.writeInt(enchantmentLevel);
    }

    public static SyncHomingData decode(final FriendlyByteBuf buffer) {
        return new SyncHomingData(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(final SyncHomingData packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientProxy.handleSyncHomingData(packet.projectileId, packet.targetId, packet.enchantmentLevel));
        context.setPacketHandled(true);
    }
}
