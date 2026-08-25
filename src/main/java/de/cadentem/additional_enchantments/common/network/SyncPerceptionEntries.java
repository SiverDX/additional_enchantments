package de.cadentem.additional_enchantments.common.network;

import com.mojang.serialization.Codec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SyncPerceptionEntries(Map<Integer, ShiftingColor.Mapped> entries) implements CustomPacketPayload {
    public static final Type<SyncPerceptionEntries> TYPE = new Type<>(AE.location("sync_perception_entries"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPerceptionEntries> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Codec.unboundedMap(Codec.INT, ShiftingColor.Mapped.CODEC)), SyncPerceptionEntries::entries,
            SyncPerceptionEntries::new
    );

    public static void handleClient(final SyncPerceptionEntries packet, final IPayloadContext context) {
        context.enqueueWork(() -> AE.PROXY.setPerceptionEntries(packet.entries()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
