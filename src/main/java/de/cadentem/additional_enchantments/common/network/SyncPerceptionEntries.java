package de.cadentem.additional_enchantments.common.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SyncPerceptionEntries(Map<Integer, ShiftingColor.Mapped> entries) implements CustomPacketPayload {
    public static final Type<SyncPerceptionEntries> TYPE = new Type<>(AE.location("sync_perception_entries"));

    /** By default, Minecraft has only support for maps with String keys */
    private static final Codec<List<Pair<Integer, ShiftingColor.Mapped>>> PAIRS = Codec.mapPair(Codec.INT.fieldOf("key"), ShiftingColor.Mapped.CODEC.fieldOf("value")).codec().listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPerceptionEntries> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(PAIRS.xmap(
                    pairs -> pairs.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
                    map -> map.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList()
            )), SyncPerceptionEntries::entries,
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
