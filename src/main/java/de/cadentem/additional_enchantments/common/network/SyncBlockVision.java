package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.BlockVisionData;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncBlockVision(List<BlockVision.Mapped> visions, boolean remove) implements CustomPacketPayload {
    public static final Type<SyncBlockVision> TYPE = new Type<>(AE.location("sync_block_vision"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockVision> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(BlockVision.Mapped.CODEC.listOf()), SyncBlockVision::visions,
            ByteBufCodecs.BOOL, SyncBlockVision::remove,
            SyncBlockVision::new
    );

    public static void handleClient(final SyncBlockVision packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockVisionData data = context.player().getData(AEDataAttachments.BLOCK_VISION);

            if (packet.remove()) {
                data.removeVisions(packet.visions().stream().map(BlockVision.Mapped::id).toList());
            } else {
                data.addVisions(packet.visions());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
