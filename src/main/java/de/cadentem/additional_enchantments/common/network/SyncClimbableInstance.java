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

public record SyncClimbableInstance(Climbable climbable, boolean remove) implements CustomPacketPayload {
    public static final Type<SyncClimbableInstance> TYPE = new Type<>(AE.location("sync_climbable_instance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClimbableInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Climbable.CODEC), SyncClimbableInstance::climbable,
            ByteBufCodecs.BOOL, SyncClimbableInstance::remove,
            SyncClimbableInstance::new
    );

    public static void handleClient(final SyncClimbableInstance packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClimbableData data = context.player().getData(AEDataAttachments.CLIMBABLE);

            if (packet.remove()) {
                data.removeClimbable(packet.climbable());
            } else {
                data.addClimbable(packet.climbable());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
