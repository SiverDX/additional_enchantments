package de.cadentem.additional_enchantments.mixin.client;

import de.cadentem.additional_enchantments.client.treasure_finder.TreasureFinderHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    /** Update the handler while a search is pending or not scheduled */
    @Inject(method = "sendBlockUpdated", at = @At("HEAD"))
    private void additional_enchantments$updateTreasureFinder(final BlockPos position, final BlockState oldState, final BlockState newState, final int flags, final CallbackInfo callback) {
        TreasureFinderHandler.updateEntry(position, oldState, newState);
    }
}