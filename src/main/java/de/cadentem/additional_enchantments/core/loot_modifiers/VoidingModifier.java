package de.cadentem.additional_enchantments.core.loot_modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.VoidingEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class VoidingModifier extends LootModifier {
    public static final String ID = "voiding";
    public static final Codec<VoidingModifier> CODEC = RecordCodecBuilder.create(instance -> LootModifier.codecStart(instance).apply(instance, VoidingModifier::new));

    public VoidingModifier(final LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, final LootContext context) {
        if (!context.hasParam(LootContextParams.BLOCK_STATE)) {
            return generatedLoot;
        }

        BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
        ItemStack tool = context.getParam(LootContextParams.TOOL);

        if (context.hasParam(LootContextParams.THIS_ENTITY)) {
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);

            if (entity instanceof Player) {
                AtomicBoolean shouldSkip = new AtomicBoolean(false);
                PlayerDataProvider.getCapability(entity).ifPresent(data -> shouldSkip.set(data.voidingState == VoidingEnchantment.State.DISABLED));

                if (shouldSkip.get()) {
                    return generatedLoot;
                }
            }
        }

        if (state.is(AEBlockTags.VOIDING)) {
            int enchantmentLevel = tool.getEnchantmentLevel(AEEnchantments.VOIDING.get());

            if (enchantmentLevel > 0) {
                // Only remove the relevant block items from the dropped loot
                ITagManager<Block> tagManager = ForgeRegistries.BLOCKS.tags();

                if (tagManager != null) {
                    ITag<Block> entries = tagManager.getTag(AEBlockTags.VOIDING);

                    for (int i = 0; i < generatedLoot.size(); i++) {
                        Item item = generatedLoot.get(i).getItem();

                        if (item instanceof BlockItem blockItem) {
                            if (entries.contains(blockItem.getBlock())) {
                                generatedLoot.remove(i);
                                i--;
                            }
                        }
                    }
                } else {
                    generatedLoot.clear();
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
