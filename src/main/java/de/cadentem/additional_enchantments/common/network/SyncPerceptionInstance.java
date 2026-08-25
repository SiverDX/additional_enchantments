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

public record SyncPerceptionInstance(Perception perception, boolean remove) implements CustomPacketPayload {
    public static final Type<SyncPerceptionInstance> TYPE = new Type<>(AE.location("sync_perception_instance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPerceptionInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Perception.CODEC), SyncPerceptionInstance::perception,
            ByteBufCodecs.BOOL, SyncPerceptionInstance::remove,
            SyncPerceptionInstance::new
    );

    public static void handleClient(final SyncPerceptionInstance packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            PerceptionData data = context.player().getData(AEDataAttachments.PERCEPTION);

            if (packet.remove()) {
                data.removePerception(packet.perception());
            } else {
                data.addPerception(packet.perception());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
