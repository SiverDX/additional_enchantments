package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.enchantments.climbing.Climbable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncClimbable(List<Climbable> climbable, boolean remove) implements CustomPacketPayload {
    public static final Type<SyncClimbable> TYPE = new Type<>(AE.location("sync_climbable"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClimbable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Climbable.CODEC.listOf()), SyncClimbable::climbable,
            ByteBufCodecs.BOOL, SyncClimbable::remove,
            SyncClimbable::new
    );

    public static void handleClient(final SyncClimbable packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClimbableData data = context.player().getData(AEDataAttachments.CLIMBABLE);

            if (packet.remove()) {
                data.removeClimbables(packet.climbable().stream().map(Climbable::id).toList());
            } else {
                data.addClimbables(packet.climbable());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
