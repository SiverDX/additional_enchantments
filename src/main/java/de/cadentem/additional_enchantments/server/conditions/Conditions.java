package de.cadentem.additional_enchantments.server.conditions;

import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

import java.util.Optional;

public class Conditions {
    private static final ContextKeySet HOMING_CONTEXT = new ContextKeySet.Builder()
            .optional(LootContextParams.ATTACKING_ENTITY)
            .required(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .build();

    public static LootContext perceptionContext(final ServerLevel level, final Entity target) {
        LootParams parameters = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .create(LootContextParamSets.SELECTOR);
        return new LootContext.Builder(parameters).create(Optional.empty());
    }

    public static LootContext homingContext(final ServerLevel level, final Projectile projectile, final Entity target) {
        LootParams parameters = new LootParams.Builder(level)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, projectile.getOwner())
                .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, projectile)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .create(HOMING_CONTEXT);
        return new LootContext.Builder(parameters).create(Optional.empty());
    }

    public static LootItemCondition.Builder thisEntity(final EntityPredicate predicate) {
        return ofEntity(predicate, LootContext.EntityTarget.THIS);
    }

    public static LootItemCondition.Builder ofEntity(final EntityPredicate predicate, LootContext.EntityTarget target) {
        return LootItemEntityPropertyCondition.hasProperties(target, predicate);
    }

    public static ContextAwarePredicate none() {
        return EntityPredicate.wrap(EntityPredicate.Builder.entity().build());
    }
}
