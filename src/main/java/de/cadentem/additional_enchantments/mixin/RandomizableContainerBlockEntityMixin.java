package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.network.SyncLootTable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BaseContainerBlockEntity {
    protected RandomizableContainerBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @Inject(method = "unpackLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V"))
    private void dragonSurvival$setLootTable(final Player pPlayer, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            NetworkHandler.CHANNEL.send(PacketDistributor.DIMENSION.with(serverLevel::dimension), new SyncLootTable(SyncLootTable.EMPTY, 0, worldPosition));
        }
    }
}
