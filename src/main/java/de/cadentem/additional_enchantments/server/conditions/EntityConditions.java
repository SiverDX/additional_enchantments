package de.cadentem.additional_enchantments.server.conditions;

import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public class EntityConditions {
    public static EntityPredicate isItemEquipped(final EquipmentSlot equipmentSlot, final TagKey<Item> tag) {
        EntityEquipmentPredicate.Builder builder = EntityEquipmentPredicate.Builder.equipment();

        switch (equipmentSlot) {
            case MAINHAND -> builder.mainhand(ItemPredicate.Builder.item().of(tag));
            case OFFHAND -> builder.offhand(ItemPredicate.Builder.item().of(tag));
            case FEET -> builder.feet(ItemPredicate.Builder.item().of(tag));
            case LEGS -> builder.legs(ItemPredicate.Builder.item().of(tag));
            case CHEST -> builder.chest(ItemPredicate.Builder.item().of(tag));
            case HEAD -> builder.head(ItemPredicate.Builder.item().of(tag));
            case BODY -> builder.body(ItemPredicate.Builder.item().of(tag));
            default -> throw new IllegalArgumentException("Invalid equipment slot: " + equipmentSlot);
        }

        return EntityPredicate.Builder.entity().equipment(builder.build()).build();
    }

    public static LootItemCondition isAnyItemEquipped(final TagKey<Item> tag, final LootContext.EntityTarget target) {
        AnyOfCondition.Builder builder = new AnyOfCondition.Builder();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            builder.or(LootItemEntityPropertyCondition.hasProperties(target, isItemEquipped(slot, tag)));
        }

        return builder.build();
    }

    public static EntityPredicate isType(final EntityType<?> type) {
        return EntityPredicate.Builder.entity().of(type).build();
    }

    public static EntityPredicate isType(final TagKey<EntityType<?>> tag) {
        return EntityPredicate.Builder.entity().of(tag).build();
    }
}
