package de.cadentem.additional_enchantments.common.network;

import com.mojang.serialization.Codec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record SyncClimbablePositions(Set<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<SyncClimbablePositions> TYPE = new Type<>(AE.location("sync_climbable_positions"));

    public static final StreamCodec<FriendlyByteBuf, SyncClimbablePositions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.list(BlockPos.CODEC)).map(HashSet::new, ArrayList::new), SyncClimbablePositions::positions,
            SyncClimbablePositions::new
    );

    public static void handleServer(final SyncClimbablePositions packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.level() instanceof WorldGenLevel level)) {
                return;
            }

            ClimbableData data = player.getExistingData(AEDataAttachments.CLIMBABLE_DATA).orElse(null);

            if (data != null) {
                data.setTrackedClimbPositions(packet.positions());
            }

            context.reply(new SyncClimbablePositions(ClimbingHandler.filterPositions(data, level, player, packet.positions())));
        });
    }

    public static void handleClient(final SyncClimbablePositions packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClimbableData data = context.player().getExistingData(AEDataAttachments.CLIMBABLE_DATA).orElse(null);

            if (data == null) {
                return;
            }

            data.setApprovedClimbPositions(packet.positions());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
