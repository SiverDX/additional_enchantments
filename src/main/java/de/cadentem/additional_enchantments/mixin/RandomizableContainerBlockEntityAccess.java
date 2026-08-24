package de.cadentem.additional_enchantments.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RandomizableContainerBlockEntity.class)
public interface RandomizableContainerBlockEntityAccess {
    @Accessor("lootTable")
    ResourceKey<LootTable> additional_enchantments$getLootTable();

    @Invoker("setLootTable")
    void additional_enchantments$setLootTable(final ResourceKey<LootTable> lootTable);
}
