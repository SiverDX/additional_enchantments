package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.FluidVisionData;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncFluidVision(List<FluidVision.Mapped> visions, boolean remove) implements CustomPacketPayload {
    public static final Type<SyncFluidVision> TYPE = new Type<>(AE.location("sync_fluid_vision"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFluidVision> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(FluidVision.Mapped.CODEC.listOf()), SyncFluidVision::visions,
            ByteBufCodecs.BOOL, SyncFluidVision::remove,
            SyncFluidVision::new
    );

    public static void handleClient(final SyncFluidVision packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            FluidVisionData data = context.player().getData(AEDataAttachments.FLUID_VISION);

            if (packet.remove()) {
                data.removeVisions(packet.visions().stream().map(FluidVision.Mapped::id).toList());
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
