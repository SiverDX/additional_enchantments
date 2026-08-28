package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.enchantments.climbing.Climbable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncClimbable(int entityId, List<Climbable> climbable, NetworkHandler.SyncType syncType) implements CustomPacketPayload {
    public static final Type<SyncClimbable> TYPE = new Type<>(AE.location("sync_climbable"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncClimbable> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncClimbable::entityId,
            ByteBufCodecs.fromCodecWithRegistries(Climbable.CODEC.listOf()), SyncClimbable::climbable,
            ByteBufCodecs.fromCodec(NetworkHandler.SyncType.CODEC), SyncClimbable::syncType,
            SyncClimbable::new
    );

    public static void handleClient(final SyncClimbable packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.entityId()) instanceof Entity entity) {
                ClimbableData data = entity.getData(AEDataAttachments.CLIMBABLE);

                switch (packet.syncType()) {
                    case COMPLETE -> data.setEntries(packet.climbable());
                    case ADD -> data.addClimbables(packet.climbable());
                    case REMOVE -> data.removeClimbables(packet.climbable().stream().map(Climbable::id).toList());
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
