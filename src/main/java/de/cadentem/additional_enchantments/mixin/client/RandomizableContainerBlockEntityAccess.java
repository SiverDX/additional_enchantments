package de.cadentem.additional_enchantments.mixin.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RandomizableContainerBlockEntity.class)
public interface RandomizableContainerBlockEntityAccess {
    @Accessor("lootTable")
    ResourceLocation additional_enchantments$getLootTable();

    @Invoker("setLootTable")
    void additional_enchantments$setLootTable(final ResourceLocation lootTable, final long seed);
}
