package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.TreasureFinderData;
import de.cadentem.additional_enchantments.enchantments.treasure_finder.TreasureFinder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncTreasureFinder(List<TreasureFinder.Mapped> visions, NetworkHandler.SyncType syncType) implements CustomPacketPayload {
    public static final Type<SyncTreasureFinder> TYPE = new Type<>(AE.location("sync_treasure_finder"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTreasureFinder> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(TreasureFinder.Mapped.CODEC.listOf()), SyncTreasureFinder::visions,
            ByteBufCodecs.fromCodec(NetworkHandler.SyncType.CODEC), SyncTreasureFinder::syncType,
            SyncTreasureFinder::new
    );

    public static void handleClient(final SyncTreasureFinder packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            TreasureFinderData data = context.player().getData(AEDataAttachments.TREASURE_FINDER);

            switch (packet.syncType()) {
                case COMPLETE -> data.setVisions(packet.visions());
                case ADD -> data.addVisions(packet.visions());
                case REMOVE -> data.removeVisions(packet.visions().stream().map(TreasureFinder.Mapped::id).toList());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
