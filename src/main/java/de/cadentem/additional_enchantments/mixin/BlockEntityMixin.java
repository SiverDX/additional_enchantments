package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
    @ModifyReturnValue(method = "getUpdateTag", at = @At("RETURN"))
    private CompoundTag additional_enchantments$storeLootTable(final CompoundTag tag, @Local(argsOnly = true) final HolderLookup.Provider provider) {
        if ((Object) this instanceof RandomizableContainerBlockEntityAccess access) {
            ResourceKey<LootTable> key = access.additional_enchantments$getLootTable();

            if (key != null) {
                ResourceKey.codec(Registries.LOOT_TABLE).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), key)
                        .resultOrPartial(AE.LOG::error)
                        .ifPresent(data -> tag.put(AE.MODID + ".loot_table", data));
            }
        }

        return tag;
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void additional_enchantments$loadLootTable(final CompoundTag tag, final HolderLookup.Provider provider, final CallbackInfo callback) {
        if ((Object) this instanceof RandomizableContainerBlockEntityAccess access && tag.contains(AE.MODID + ".loot_table")) {
            ResourceKey.codec(Registries.LOOT_TABLE).decode(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(AE.MODID + ".loot_table"))
                    .resultOrPartial(AE.LOG::error)
                    .ifPresent(data -> access.additional_enchantments$setLootTable(data.getFirst()));
        }
    }
}
