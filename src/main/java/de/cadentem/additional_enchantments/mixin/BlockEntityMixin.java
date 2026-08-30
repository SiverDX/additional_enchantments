package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
    @ModifyReturnValue(method = "getUpdateTag", at = @At("RETURN"))
    private CompoundTag additional_enchantments$storeLootTable(final CompoundTag tag, @Local(argsOnly = true, name = "registries") final HolderLookup.Provider provider) {
        if ((Object) this instanceof RandomizableContainerAccess access) {
            ResourceKey<LootTable> key = access.additional_enchantments$getLootTable();

            if (key == null) {
                return tag;
            }
            
            ResourceKey.codec(Registries.LOOT_TABLE).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), key)
                    .resultOrPartial(AE.LOG::error)
                    .ifPresent(data -> tag.put(AE.MODID + ".loot_table", data));
        }

        return tag;
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void additional_enchantments$loadLootTable(final ValueInput input, final CallbackInfo callback) {
        if (!((Object) this instanceof RandomizableContainerAccess access)) {
            return;
        }
        
        // Reading it through the codec directly doesn't work since it's considered a String now
        input.getString(AE.MODID + ".loot_table").ifPresent(key -> {
            Identifier identifier = Identifier.tryParse(key);
            
            if (identifier == null) {
                access.additional_enchantments$setLootTable(null);
            } else {
                access.additional_enchantments$setLootTable(ResourceKey.create(Registries.LOOT_TABLE, identifier));
            }
        });
    }
}
