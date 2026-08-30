package de.cadentem.additional_enchantments.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerAccess {
    @Invoker("getLootTable")
    @Nullable ResourceKey<LootTable> additional_enchantments$getLootTable();

    @Invoker("setLootTable")
    void additional_enchantments$setLootTable(final ResourceKey<LootTable> lootTable);
}
