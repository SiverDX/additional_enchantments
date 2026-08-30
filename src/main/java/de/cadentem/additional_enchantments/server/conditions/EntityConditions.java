package de.cadentem.additional_enchantments.server.conditions;

import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityConditions {
    public static EntityPredicate isType(final BootstrapContext<?> context, final EntityType<?> type) {
        return isType(context.lookup(Registries.ENTITY_TYPE), type);
    }

    public static EntityPredicate isType(final HolderGetter<EntityType<?>> lookup, final EntityType<?> type) {
        return EntityPredicate.Builder.entity().of(lookup, type).build();
    }

    public static EntityPredicate isType(final BootstrapContext<?> context, final TagKey<EntityType<?>> tag) {
        return isType(context.lookup(Registries.ENTITY_TYPE), tag);
    }

    public static EntityPredicate isType(final HolderGetter<EntityType<?>> lookup, final TagKey<EntityType<?>> tag) {
        return EntityPredicate.Builder.entity().of(lookup, tag).build();
    }
}
