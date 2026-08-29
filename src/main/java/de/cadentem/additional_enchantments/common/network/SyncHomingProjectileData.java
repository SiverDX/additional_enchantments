package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ProjectileHomingData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncHomingProjectileData(int projectileId, int targetId, float velocityMultiplier, float maxTurnPerTick) implements CustomPacketPayload {
    public static final Type<SyncHomingProjectileData> TYPE = new Type<>(AE.location("sync_homing_projectile_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHomingProjectileData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncHomingProjectileData::projectileId,
            ByteBufCodecs.INT, SyncHomingProjectileData::targetId,
            ByteBufCodecs.FLOAT, SyncHomingProjectileData::velocityMultiplier,
            ByteBufCodecs.FLOAT, SyncHomingProjectileData::maxTurnPerTick,
            SyncHomingProjectileData::new
    );

    public static void handleClient(final SyncHomingProjectileData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.projectileId()) instanceof Projectile projectile) {
                ProjectileHomingData data = projectile.getData(AEDataAttachments.PROJECTILE_HOMING_DATA);
                data.setClientData(context.player().level().getEntity(packet.targetId()), packet.targetId(), packet.velocityMultiplier(), packet.maxTurnPerTick());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
