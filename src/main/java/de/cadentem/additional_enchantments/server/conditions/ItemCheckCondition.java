package de.cadentem.additional_enchantments.server.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ItemCheckCondition(Optional<HolderSet<Item>> items, LootContext.EntityTarget entityTarget, Optional<List<EnchantmentInstance>> enchantments) implements LootItemCondition {
    // TODO :: rethink options for when all items are valid etc.
    public static final MapCodec<ItemCheckCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items").forGetter(ItemCheckCondition::items),
            LootContext.EntityTarget.CODEC.optionalFieldOf("entity", LootContext.EntityTarget.THIS).forGetter(ItemCheckCondition::entityTarget),
            EnchantmentInstance.CODEC.listOf().optionalFieldOf("enchantments").forGetter(ItemCheckCondition::enchantments)
    ).apply(instance, ItemCheckCondition::new));

    @Override
    public boolean test(final LootContext context) {
        if (!(context.getParamOrNull(entityTarget.getParam()) instanceof ItemEntity itemEntity)) {
            return false;
        }

        ItemStack item = itemEntity.getItem();

        if (items.map(items -> !item.is(items)).orElse(false)) {
            return false;
        }

        if (enchantments.isEmpty()) {
            return true;
        }

        List<EnchantmentInstance> enchantments = this.enchantments.get();

        if (enchantments.isEmpty()) {
            return true;
        }

        for (EnchantmentInstance instance : enchantments) {
            for (Holder<Enchantment> enchantment : instance.enchantments()) {
                if (item.getEnchantmentLevel(enchantment) < instance.level()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return AELootItemConditions.ITEM_CHECK.value();
    }

    public record EnchantmentInstance(HolderSet<Enchantment> enchantments, int level) {
        public static final Codec<EnchantmentInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(EnchantmentInstance::enchantments),
                Codec.INT.fieldOf("level").forGetter(EnchantmentInstance::level)
        ).apply(instance, EnchantmentInstance::new));
    }
}
