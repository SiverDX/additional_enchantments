package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.HomingData;
import de.cadentem.additional_enchantments.enchantments.homing.Homing;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncHoming(int entityId, List<Homing.Mapped> visions, NetworkHandler.SyncType syncType) implements CustomPacketPayload {
    public static final Type<SyncHoming> TYPE = new Type<>(AE.location("sync_homing"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHoming> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncHoming::entityId,
            ByteBufCodecs.fromCodecWithRegistries(Homing.Mapped.CODEC.listOf()), SyncHoming::visions,
            ByteBufCodecs.fromCodec(NetworkHandler.SyncType.CODEC), SyncHoming::syncType,
            SyncHoming::new
    );

    public static void handleClient(final SyncHoming packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.entityId()) instanceof Entity entity) {
                HomingData data = entity.getData(AEDataAttachments.HOMING);

                switch (packet.syncType()) {
                    case COMPLETE -> data.setEntries(packet.visions());
                    case ADD -> data.addHoming(packet.visions());
                    case REMOVE -> data.removeHoming(packet.visions().stream().map(Homing.Mapped::id).toList());
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
