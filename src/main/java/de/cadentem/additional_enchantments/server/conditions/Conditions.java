package de.cadentem.additional_enchantments.server.conditions;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.util.Optional;

public class Conditions {
    private static final LootContextParamSet CONTEXT = new LootContextParamSet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .build();

    public static LootContext createContext(final ServerLevel level, final Entity target) {
        LootParams parameters = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .create(CONTEXT);
        return new LootContext.Builder(parameters).create(Optional.empty());
    }

    public static LootItemCondition.Builder thisEntity(final EntityPredicate predicate) {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, predicate);
    }

    public static ContextAwarePredicate none() {
        return EntityPredicate.wrap(EntityPredicate.Builder.entity().build());
    }
}
