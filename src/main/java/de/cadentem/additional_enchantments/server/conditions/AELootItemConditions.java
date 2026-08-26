package de.cadentem.additional_enchantments.server.conditions;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AELootItemConditions {
    public static final DeferredRegister<LootItemConditionType> REGISTRY = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, AE.MODID);
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ENTITY_TYPE = REGISTRY.register("entity_type", () -> new LootItemConditionType(EntityTypeCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> MATCH_ITEM_ENTITY = REGISTRY.register("match_item_entity", () -> new LootItemConditionType(MatchItemEntityCondition.CODEC));
}
