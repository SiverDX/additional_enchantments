package de.cadentem.additional_enchantments.enchantments;

import com.mojang.datafixers.util.Either;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVisionEffect;
import de.cadentem.additional_enchantments.enchantments.block_vision.LevelBasedBlockVision;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

public class AEEnchantments {
    public static ResourceKey<Enchantment> BLOCK_VISION = key("block_vision");

    public static void bootstrap(final BootstrapContext<Enchantment> context) {
        //noinspection DataFlowIssue -> color is present
        context.register(BLOCK_VISION, new Enchantment(
                Component.translatable("enchantment.additional_enchantments.block_vision.desc"),
                new Enchantment.EnchantmentDefinition(
                        context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR),
                        Optional.empty(),
                        1,
                        1,
                        Enchantment.constantCost(10),
                        Enchantment.constantCost(25),
                        1,
                        List.of(EquipmentSlotGroup.HEAD)
                ),
                HolderSet.empty(), // TODO
                DataComponentMap.builder().set(AEEnchantmentRegistry.BLOCK_VISION_COMPONENT.value(), new BlockVisionEffect(
                        LevelBasedBlockVision.atLevel(1, new BlockVision(
                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.DIAMOND_ORES)),
                                BlockVision.DisplayType.SIMPLE_SHADER,
                                15,
                                List.of(
                                        new BlockVision.ColorEntry(TextColor.fromLegacyFormat(ChatFormatting.GOLD), 0.5f),
                                        new BlockVision.ColorEntry(TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE), 0.5f),
                                        new BlockVision.ColorEntry(TextColor.fromLegacyFormat(ChatFormatting.GREEN), 0.5f),
                                        new BlockVision.ColorEntry(TextColor.fromLegacyFormat(ChatFormatting.RED), 0.5f),
                                        new BlockVision.ColorEntry(TextColor.fromLegacyFormat(ChatFormatting.BLUE), 0.5f)
                                ),
                                10,
                                0.5f
                        ))
                )).build()));
    }

    private static ResourceKey<Enchantment> key(final String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, AE.location(path));
    }
}
