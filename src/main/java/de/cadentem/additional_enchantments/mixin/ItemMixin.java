package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyVariable(method = "verifyTagAfterLoad", at = @At("HEAD"), argsOnly = true)
    private CompoundTag additional_enchantments$fixEnchantment(final CompoundTag tag) {
        for (Tag enchantmentTag : tag.getList("Enchantments", ListTag.TAG_COMPOUND)) {
            if (enchantmentTag instanceof CompoundTag compound) {
                // Enchantment was removed
                if (compound.getString("id").equals(AE.MODID + ":" + AEEnchantments.ORE_SIGHT_ID)) {
                    compound.putString("id", AE.MODID + ":" + AEEnchantments.TREASURE_FINDER_ID);
                }
            }
        }

        return tag;
    }
}
