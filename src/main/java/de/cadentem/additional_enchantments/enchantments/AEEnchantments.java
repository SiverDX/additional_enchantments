package de.cadentem.additional_enchantments.enchantments;

import com.mojang.datafixers.util.Either;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVisionEffect;
import de.cadentem.additional_enchantments.enchantments.block_vision.LevelBasedBlockVision;
import de.cadentem.additional_enchantments.enchantments.climbing.Climbable;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbableEffect;
import de.cadentem.additional_enchantments.enchantments.climbing.LevelBasedClimbable;
import de.cadentem.additional_enchantments.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;

public class AEEnchantments {
    public static ResourceKey<Enchantment> BLOCK_VISION = key("block_vision");
    public static ResourceKey<Enchantment> CLIMBABLE = key("climbable");

    public static void bootstrap(final BootstrapContext<Enchantment> context) {
        context.register(CLIMBABLE, new Enchantment(
                Component.translatable("enchantment.additional_enchantments.climbable"),
                new Enchantment.EnchantmentDefinition(
                        context.lookup(Registries.ITEM).getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        Optional.empty(),
                        1,
                        3,
                        Enchantment.dynamicCost(20, 10),
                        Enchantment.dynamicCost(50, 10),
                        1,
                        List.of(EquipmentSlotGroup.FEET)
                ),
                HolderSet.empty(), // TODO
                DataComponentMap.builder().set(
                        AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value(),
                        ClimbableEffect.single(
                                new LevelBasedClimbable.Entry(
                                        new Climbable(
                                                AE.location("climbable_enchantment"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY)),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.noFluid()
                                                ),
                                                false,
                                                false
                                        ),
                                        MinMaxBounds.Ints.between(1, 1)
                                ),
                                new LevelBasedClimbable.Entry(
                                        new Climbable(
                                                AE.location("climbable_enchantment"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY)),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.noFluid()
                                                ),
                                                true,
                                                false
                                        ),
                                        MinMaxBounds.Ints.between(2, 2)
                                ),
                                new LevelBasedClimbable.Entry(
                                        new Climbable(
                                                AE.location("climbable_enchantment"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY)),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.noFluid()
                                                ),
                                                true,
                                                true
                                        ),
                                        MinMaxBounds.Ints.between(3, 3)
                                )
                        )
                ).build()));

        context.register(BLOCK_VISION, new Enchantment(
                Component.translatable("enchantment.additional_enchantments.block_vision"),
                new Enchantment.EnchantmentDefinition(
                        context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        Optional.empty(),
                        1,
                        4,
                        Enchantment.dynamicCost(20, 10),
                        Enchantment.dynamicCost(50, 10),
                        1,
                        List.of(EquipmentSlotGroup.HEAD)
                ),
                HolderSet.empty(), // TODO
                DataComponentMap.builder().set(
                        AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value(),
                        BlockVisionEffect.single(
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.COPPER_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of("#7a4a2e", 0.15f),
                                                        Color.of(ChatFormatting.DARK_GREEN, 0.15f),
                                                        Color.of("#3f7f5f", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(1, 1)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.IRON_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of(ChatFormatting.WHITE, 0.15f),
                                                        Color.of(ChatFormatting.GRAY, 0.15f),
                                                        Color.of(ChatFormatting.DARK_GRAY, 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(1, 2)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.REDSTONE_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of(ChatFormatting.DARK_RED, 0.15f),
                                                        Color.of(ChatFormatting.RED, 0.15f),
                                                        Color.of("#ff4d4d", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(1, 2)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(AEBlockTags.ZINC_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of("#9aa3ad", 0.15f),
                                                        Color.of("#b7c0c9", 0.15f),
                                                        Color.of("#d0d7df", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(1, 2)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(AEBlockTags.SILVER_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of(ChatFormatting.WHITE, 0.15f),
                                                        Color.of(ChatFormatting.GRAY, 0.15f),
                                                        Color.of("#dfe6ee", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(1, 2)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.LAPIS_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of(ChatFormatting.DARK_BLUE, 0.15f),
                                                        Color.of(ChatFormatting.BLUE, 0.15f),
                                                        Color.of("#4169e1", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(2, 3)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.GOLD_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                24,
                                                List.of(
                                                        Color.of(ChatFormatting.GOLD, 0.15f),
                                                        Color.of(ChatFormatting.YELLOW, 0.15f),
                                                        Color.of("#ffdd55", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(2, 3)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.EMERALD_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                16,
                                                List.of(
                                                        Color.of(ChatFormatting.DARK_GREEN, 0.15f),
                                                        Color.of(ChatFormatting.GREEN, 0.15f),
                                                        Color.of("#55ff88", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(3, 3)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.DIAMOND_ORES)),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                16,
                                                List.of(
                                                        Color.of("#3fffdc", 0.15f),
                                                        Color.of(ChatFormatting.AQUA, 0.15f),
                                                        Color.of("#3fc5ff", 0.15f),
                                                        Color.of("#8b7dff", 0.15f),
                                                        Color.of("#55ff88", 0.15f)
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.between(3, 3)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.EMERALD_ORES)),
                                                BlockVision.DisplayType.OUTLINE,
                                                16,
                                                List.of(
                                                        Color.of(ChatFormatting.DARK_GREEN),
                                                        Color.of(ChatFormatting.GREEN),
                                                        Color.of("#55ff88")
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.atLeast(4)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.DIAMOND_ORES)),
                                                BlockVision.DisplayType.OUTLINE,
                                                16,
                                                List.of(
                                                        Color.of("#3fffdc"),
                                                        Color.of(ChatFormatting.AQUA),
                                                        Color.of("#3fc5ff"),
                                                        Color.of("#8b7dff"),
                                                        Color.of("#55ff88")
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.atLeast(4)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_NETHERITE_SCRAP)),
                                                BlockVision.DisplayType.OUTLINE,
                                                16,
                                                List.of(
                                                        Color.of(ChatFormatting.DARK_GRAY),
                                                        Color.of("#3b3b3b"),
                                                        Color.of("#6e4a3a")
                                                ),
                                                0,
                                                1
                                        ),
                                        MinMaxBounds.Ints.atLeast(4)
                                ),
                                new LevelBasedBlockVision.Entry(
                                        new BlockVision(
                                                Either.left(BlockVision.SpecialBlockType.TREASURES),
                                                BlockVision.DisplayType.PARTICLES,
                                                24,
                                                List.of(
                                                        Color.of("#a87c1a"),
                                                        Color.of(ChatFormatting.GOLD),
                                                        Color.of("#ff9f1a"),
                                                        Color.of("#ff5fd2"),
                                                        Color.of("#5fd9ff"),
                                                        Color.of("#ffffff")
                                                ),
                                                10,
                                                1
                                        ),
                                        MinMaxBounds.Ints.atLeast(1)
                                )
                        )
                ).build()));
    }

    private static ResourceKey<Enchantment> key(final String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, AE.location(path));
    }
}
