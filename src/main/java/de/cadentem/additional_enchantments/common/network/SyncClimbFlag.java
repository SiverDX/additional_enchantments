package de.cadentem.additional_enchantments.common.network;

import com.mojang.serialization.Codec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncClimbFlag(int entityId, ClimbingType climbingType) implements CustomPacketPayload {
    public static final Type<SyncClimbFlag> TYPE = new Type<>(AE.location("sync_climb_flag"));

    public static final StreamCodec<FriendlyByteBuf, SyncClimbFlag> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncClimbFlag::entityId,
            ByteBufCodecs.fromCodec(ClimbingType.CODEC), SyncClimbFlag::climbingType,
            SyncClimbFlag::new
    );

    public static void handleClient(final SyncClimbFlag packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.entityId()) instanceof LivingEntity entity) {
                EntityDimensions oldDimensions = entity.getDimensions(entity.getPose());

                ClimbableData data = entity.getData(AEDataAttachments.CLIMBABLE_DATA);
                data.climbingType = packet.climbingType();

                entity.refreshDimensions();
//                entity.fudgePositionAfterSizeChange(oldDimensions);
            }
        });
    }

    public enum ClimbingType implements StringRepresentable {
        WALL("wall"),
        CEILING("ceiling"),
        NONE("none");

        public static final Codec<ClimbingType> CODEC = StringRepresentable.fromValues(ClimbingType::values);

        private final String name;

        ClimbingType(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
