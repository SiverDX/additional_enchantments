package de.cadentem.additional_enchantments.network;

import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.client.VisionHandler;
import de.cadentem.additional_enchantments.mixin.client.RandomizableContainerBlockEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncLootTable(ResourceLocation loot, long seed, BlockPos worldPosition) {
    public static final ResourceLocation EMPTY = new ResourceLocation("empty");

    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(loot);
        buffer.writeLong(seed);
        buffer.writeBlockPos(worldPosition);
    }

    public static SyncLootTable decode(final FriendlyByteBuf buffer) {
        return new SyncLootTable(buffer.readResourceLocation(), buffer.readLong(), buffer.readBlockPos());
    }

    public static void handle(final SyncLootTable packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                Player player = ClientProxy.getLocalPlayer();

                if (player == null) {
                    return;
                }

                BlockEntity blockEntity = player.level.getBlockEntity(packet.worldPosition());

                if (blockEntity instanceof RandomizableContainerBlockEntityAccess access) {
                    if (packet.loot().equals(EMPTY)) {
                        access.additional_enchantments$setLootTable(null, 0);
                        VisionHandler.removeTreasure(packet.worldPosition());
                    } else {
                        access.additional_enchantments$setLootTable(packet.loot(), packet.seed());
                        VisionHandler.addTreasure(packet.worldPosition(), blockEntity.getBlockState());
                    }
                }
            });
        }

        context.setPacketHandled(true);
    }
}
