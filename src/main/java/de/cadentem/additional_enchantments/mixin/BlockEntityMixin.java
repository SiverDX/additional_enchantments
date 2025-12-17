package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
    @ModifyReturnValue(method = "getUpdateTag", at = @At("RETURN"))
    private CompoundTag additional_enchantments$getUpdateTag(final CompoundTag original) {
        if ((Object) this instanceof RandomizableContainerBlockEntityAccess access) {
            ResourceLocation lootTable = access.additional_enchantments$getLootTable();

            if (lootTable != null) {
                original.putString(AE.MODID + ".loot_table", lootTable.toString());
            }
        }

        return original;
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void additional_enchantments$loadLootTable(final CompoundTag tag, final CallbackInfo callback) {
        if ((Object) this instanceof RandomizableContainerBlockEntityAccess access && tag.contains(AE.MODID + ".loot_table")) {
            access.additional_enchantments$setLootTable(ResourceLocation.tryParse(tag.getString(AE.MODID + ".loot_table")), 0);
        }
    }
}
