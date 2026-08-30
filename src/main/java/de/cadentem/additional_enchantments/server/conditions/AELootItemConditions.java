package de.cadentem.additional_enchantments.server.conditions;

import com.mojang.serialization.MapCodec;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AELootItemConditions {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> REGISTRY = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, AE.MODID);

    static {
        REGISTRY.register("entity_type", () -> EntityTypeCondition.CODEC);
        REGISTRY.register("match_item_entity", () -> MatchItemEntityCondition.CODEC);
    }
}
