package de.cadentem.additional_enchantments.server.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record MatchItemEntityCondition(Optional<ItemPredicate> predicate, LootContext.EntityTarget target) implements LootItemCondition {
    public static final MapCodec<MatchItemEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchItemEntityCondition::predicate),
            LootContext.EntityTarget.CODEC.optionalFieldOf("target", LootContext.EntityTarget.THIS).forGetter(MatchItemEntityCondition::target)
    ).apply(instance, MatchItemEntityCondition::new));

    @Override
    public boolean test(final LootContext context) {
        if (!(context.getParamOrNull(target.getParam()) instanceof ItemEntity itemEntity)) {
            return false;
        }

        return predicate.map(item -> item.test(itemEntity.getItem())).orElse(true);
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return AELootItemConditions.MATCH_ITEM_ENTITY.value();
    }

    public static LootItemCondition.Builder matches(final ItemPredicate.Builder builder) {
        return () -> new MatchItemEntityCondition(Optional.of(builder.build()), LootContext.EntityTarget.THIS);
    }
}
