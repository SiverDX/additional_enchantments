package de.cadentem.additional_enchantments.common.network;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.block_vision.BlockVisionHandler;
import de.cadentem.additional_enchantments.mixin.RandomizableContainerBlockEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncLootTable(ResourceKey<LootTable> lootTable, BlockPos position) implements CustomPacketPayload {
    public static final Type<SyncLootTable> TYPE = new Type<>(AE.location("sync_loot_table"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLootTable> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE), SyncLootTable::lootTable,
            BlockPos.STREAM_CODEC, SyncLootTable::position,
            SyncLootTable::new
    );

    public static void handleClient(final SyncLootTable packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(packet.position());

            if (blockEntity instanceof RandomizableContainerBlockEntityAccess access) {
                access.additional_enchantments$setLootTable(packet.lootTable());

                if (packet.lootTable() == BuiltInLootTables.EMPTY) {
                    BlockVisionHandler.removeTreasure(packet.position());
                } else {
                    BlockVisionHandler.addTreasure(packet.position(), blockEntity.getBlockState());
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
