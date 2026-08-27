package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.PerceptionData;
import de.cadentem.additional_enchantments.enchantments.perception.Perception;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncPerception(List<Perception.Mapped> perceptions, NetworkHandler.SyncType syncType) implements CustomPacketPayload {
    public static final Type<SyncPerception> TYPE = new Type<>(AE.location("sync_perception"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPerception> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Perception.Mapped.CODEC.listOf()), SyncPerception::perceptions,
            ByteBufCodecs.fromCodec(NetworkHandler.SyncType.CODEC), SyncPerception::syncType,
            SyncPerception::new
    );

    public static void handleClient(final SyncPerception packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            PerceptionData data = context.player().getData(AEDataAttachments.PERCEPTION);

            switch (packet.syncType()) {
                case COMPLETE -> data.setEntries(packet.perceptions());
                case ADD -> data.addPerceptions(packet.perceptions());
                case REMOVE -> data.removePerceptions(packet.perceptions().stream().map(Perception.Mapped::id).toList());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
