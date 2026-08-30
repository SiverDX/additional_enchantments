package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerMixin {
    @Shadow void setLootTable(@Nullable final ResourceKey<LootTable> resourceKey);

    @ModifyReturnValue(method = "tryLoadLootTable", at = @At("RETURN"))
    private boolean additional_enchantments$loadLootTable(boolean hasLootTable, @Local(argsOnly = true, name = "base") final ValueInput input) {
        // Reading it through the codec directly doesn't work since it's considered a String now
        input.getString(AE.MODID + ".loot_table").ifPresent(key -> {
            Identifier identifier = Identifier.tryParse(key);

            if (identifier != null) {
                setLootTable(ResourceKey.create(Registries.LOOT_TABLE, identifier));
            }
        });

        // The loot table set above is only used for client rendering
        return hasLootTable;
    }
}
