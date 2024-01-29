package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.loot_modifiers.VoidingModifier;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class AELootModifiers extends GlobalLootModifierProvider {
    public AELootModifiers(final PackOutput output) {
        super(output, AE.MODID);
    }

    @Override
    protected void start() {
        add(VoidingModifier.ID, new VoidingModifier(new LootItemCondition[]{
                MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(AEEnchantments.VOIDING.get(), MinMaxBounds.Ints.atLeast(1)))).build()
        }));
    }
}