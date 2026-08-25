package de.cadentem.additional_enchantments.util;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.perception.Perception;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.Optional;

public class Conditions {
    public static final LootContextParam<Entity> TARGET_ENTITY = create("target_entity");

    // FIXME :: does not implement the needed interface, figure out alternative / if a custom lootcontext param is a problem without being part of enum
//    public static final EnumProxy<LootContext.EntityTarget> TARGET_ENTITY_PROXY = new EnumProxy<>(
//            LootContext.EntityTarget.class, "target_entity", TARGET_ENTITY;
//    );

    private static final LootContextParamSet CONTEXT = new LootContextParamSet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .optional(TARGET_ENTITY)
            .build();

    public static LootContext createContext(final ServerLevel level, final LivingEntity source, final Entity target) {
        LootParams parameters = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, source)
                .withParameter(TARGET_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, source.position())
                .create(CONTEXT);
        return new LootContext.Builder(parameters).create(Optional.empty());
    }

//    public static LootItemCondition.Builder targetEntity(final EntityPredicate predicate) {
//        return LootItemEntityPropertyCondition.hasProperties(Perception.TARGET_ENTITY, predicate);
//    }

    public static LootItemCondition.Builder thisEntity(final EntityPredicate predicate) {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, predicate);
    }

    public static LootItemCondition.Builder tool(final ItemPredicate predicate) {
        return () -> new MatchTool(Optional.of(predicate));
    }

    public static ContextAwarePredicate none() {
        return EntityPredicate.wrap(EntityPredicate.Builder.entity().build());
    }

    private static <T> LootContextParam<T> create(final String name) {
        return new LootContextParam<>(AE.location(name));
    }
}
