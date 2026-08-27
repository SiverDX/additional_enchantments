package de.cadentem.additional_enchantments.enchantments;

import com.mojang.datafixers.util.Either;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.data.AEFluidTypesTags;
import de.cadentem.additional_enchantments.data.AEItemTags;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVision;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVisionEffect;
import de.cadentem.additional_enchantments.enchantments.block_vision.LevelBasedBlockVision;
import de.cadentem.additional_enchantments.enchantments.climbing.Climbable;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbableEffect;
import de.cadentem.additional_enchantments.enchantments.climbing.LevelBasedClimbable;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVisionEffect;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.LevelBasedFluidVision;
import de.cadentem.additional_enchantments.enchantments.perception.LevelBasedPerception;
import de.cadentem.additional_enchantments.enchantments.perception.Perception;
import de.cadentem.additional_enchantments.enchantments.perception.PerceptionEffect;
import de.cadentem.additional_enchantments.server.conditions.Conditions;
import de.cadentem.additional_enchantments.server.conditions.EntityConditions;
import de.cadentem.additional_enchantments.server.conditions.EntityTypeCondition;
import de.cadentem.additional_enchantments.server.conditions.MatchItemEntityCondition;
import de.cadentem.additional_enchantments.util.Color;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Optional;

public class AEEnchantments {
    public static ResourceKey<Enchantment> BLOCK_VISION = key("block_vision");
    public static ResourceKey<Enchantment> CLIMBABLE = key("climbable");
    public static ResourceKey<Enchantment> PERCEPTION = key("perception");
    public static ResourceKey<Enchantment> FLUID_VISION = key("fluid_vision");

    public static void bootstrap(final BootstrapContext<Enchantment> context) {
        context.register(FLUID_VISION, new Enchantment(
                Component.translatable("enchantment.additional_enchantments.fluid_vision"),
                new Enchantment.EnchantmentDefinition(
                        context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        Optional.empty(),
                        1,
                        3,
                        Enchantment.dynamicCost(20, 10),
                        Enchantment.dynamicCost(50, 10),
                        1,
                        List.of(EquipmentSlotGroup.HEAD)
                ),
                HolderSet.empty(), // TODO
                DataComponentMap.builder().set(
                        AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value(),
                        FluidVisionEffect.single(
                                new LevelBasedFluidVision.Entry(List.of(
                                        new FluidVision(
                                                AE.location("fluid_vision_enchantment.water"),
                                                HolderSet.direct(NeoForgeMod.WATER_TYPE),
                                                LevelBasedValue.constant(0.35f)
                                        )
                                ), MinMaxBounds.Ints.atLeast(1)),
                                new LevelBasedFluidVision.Entry(List.of(
                                        new FluidVision(
                                                AE.location("fluid_vision_enchantment.lava"),
                                                HolderSet.direct(NeoForgeMod.LAVA_TYPE),
                                                LevelBasedValue.constant(0.35f)
                                        )
                                ), MinMaxBounds.Ints.atLeast(2)),
                                new LevelBasedFluidVision.Entry(List.of(
                                        new FluidVision(
                                                AE.location("fluid_vision_enchantment.bumblezone"),
                                                context.lookup(NeoForgeRegistries.FLUID_TYPES.key()).getOrThrow(AEFluidTypesTags.BUMBLEZONE),
                                                LevelBasedValue.constant(0.35f)
                                        ),
                                        new FluidVision(
                                                AE.location("fluid_vision_enchantment.create"),
                                                context.lookup(NeoForgeRegistries.FLUID_TYPES.key()).getOrThrow(AEFluidTypesTags.CREATE),
                                                LevelBasedValue.constant(0.35f)
                                        )
                                ), MinMaxBounds.Ints.atLeast(3))
                        )
                ).build()
        ));

        context.register(PERCEPTION, new Enchantment(
                Component.translatable("enchantment.additional_enchantments.perception"),
                new Enchantment.EnchantmentDefinition(
                        context.lookup(Registries.ITEM).getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        Optional.empty(),
                        1,
                        3,
                        Enchantment.dynamicCost(20, 10),
                        Enchantment.dynamicCost(50, 10),
                        1,
                        List.of(EquipmentSlotGroup.HEAD)
                ),
                HolderSet.empty(), // TODO
                DataComponentMap.builder().set(
                        AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value(),
                        PerceptionEffect.single(
                                new LevelBasedPerception.Entry(List.of(
                                        new Perception(
                                                AE.location("perception_enchantment.valuables"),
                                                AnyOfCondition.anyOf(
                                                        MatchItemEntityCondition.matches(ItemPredicate.Builder.item().of(AEItemTags.VALUABLES)),
                                                        MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(
                                                                ItemSubPredicates.ENCHANTMENTS,
                                                                ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY)))
                                                        ))
                                                ).build(),
                                                LevelBasedValue.perLevel(16, 8),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#55ffff"),
                                                        Color.of("#5b7cff"),
                                                        Color.of("#b45cff"),
                                                        Color.of("#ff5bd8"),
                                                        Color.of("#ffd45b"),
                                                        Color.of("#55ffff")
                                                ))
                                        )
                                ), MinMaxBounds.Ints.between(1, 2)),
                                new LevelBasedPerception.Entry(List.of(
                                        new Perception(
                                                AE.location("perception_enchantment.enemies"),
                                                new EntityTypeCondition(EntityTypeCondition.Type.ENEMY, LootContext.EntityTarget.THIS),
                                                LevelBasedValue.perLevel(16, 8),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#ff3030"),
                                                        Color.of("#ff6b35"),
                                                        Color.of("#ff4855")
                                                ), 1, 1)
                                        ),
                                        new Perception(
                                                AE.location("perception_enchantment.animals"),
                                                new EntityTypeCondition(EntityTypeCondition.Type.ANIMAL, LootContext.EntityTarget.THIS),
                                                LevelBasedValue.perLevel(16, 8),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#7cff6b"),
                                                        Color.of("#d4ff5c"),
                                                        Color.of("#4dff88")
                                                ))
                                        )
                                ), MinMaxBounds.Ints.atLeast(1)),
                                new LevelBasedPerception.Entry(List.of(
                                        new Perception(
                                                AE.location("perception_enchantment.bosses"),
                                                AnyOfCondition.anyOf(
                                                        Conditions.thisEntity(EntityConditions.isType(Tags.EntityTypes.BOSSES)),
                                                        Conditions.thisEntity(EntityConditions.isType(EntityType.WARDEN))
                                                ).build(),
                                                LevelBasedValue.perLevel(24, 8),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#5a189a"),
                                                        Color.of("#9d4edd"),
                                                        Color.of("#c77dff"),
                                                        Color.of("#7b2cbf")
                                                ), 1, 2)
                                        )
                                ), MinMaxBounds.Ints.atLeast(2)),
                                new LevelBasedPerception.Entry(List.of(
                                        new Perception(
                                                AE.location("perception_enchantment.limited_valuables"),
                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().of(AEItemTags.LIMITED_VALUABLES)).build(),
                                                LevelBasedValue.constant(32),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#55ffff"),
                                                        Color.of("#5b7cff"),
                                                        Color.of("#b45cff"),
                                                        Color.of("#ff5bd8"),
                                                        Color.of("#ffd45b"),
                                                        Color.of("#55ffff")
                                                ))
                                        ),
                                        new Perception(
                                                AE.location("perception_enchantment.enchanted_books"),
                                                AllOfCondition.allOf(
                                                        MatchItemEntityCondition.matches(ItemPredicate.Builder.item().of(Items.ENCHANTED_BOOK)),
                                                        AnyOfCondition.anyOf(
                                                                // General
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), MinMaxBounds.Ints.atLeast(2))))
                                                                )),
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING), MinMaxBounds.Ints.ANY)))
                                                                )),
                                                                // Armor
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), MinMaxBounds.Ints.atLeast(3))))
                                                                )),
                                                                // Weapons
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), MinMaxBounds.Ints.atLeast(4))))
                                                                )),
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), MinMaxBounds.Ints.atLeast(2))))
                                                                )),
                                                                // Tools
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY), MinMaxBounds.Ints.atLeast(4))))
                                                                )),
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), MinMaxBounds.Ints.atLeast(2))))
                                                                )),
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.ANY)))
                                                                )),
                                                                // Bow
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.POWER), MinMaxBounds.Ints.atLeast(4))))
                                                                )),
                                                                MatchItemEntityCondition.matches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.STORED_ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(new EnchantmentPredicate(context.lookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY), MinMaxBounds.Ints.ANY)))
                                                                ))
                                                        )
                                                ).build(),
                                                LevelBasedValue.constant(32),
                                                ShiftingColor.of(List.of(
                                                        Color.of("#5e35b1"),
                                                        Color.of("#7e57c2"),
                                                        Color.of("#8279c2"),
                                                        Color.of("#7e57c2"),
                                                        Color.of("#5e35b1")
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.atLeast(3))
                        )
                ).build()
        ));

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
                                new LevelBasedClimbable.Entry(List.of(
                                        new Climbable(
                                                AE.location("climbable_enchantment.base"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.solid(),
                                                        BlockPredicate.noFluid(),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY))
                                                ),
                                                false,
                                                false
                                        )
                                ), MinMaxBounds.Ints.between(1, 1)),
                                new LevelBasedClimbable.Entry(List.of(
                                        new Climbable(
                                                AE.location("climbable_enchantment.wall"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.solid(),
                                                        BlockPredicate.noFluid(),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY))
                                                ),
                                                true,
                                                false
                                        )
                                ), MinMaxBounds.Ints.between(2, 2)),
                                new LevelBasedClimbable.Entry(List.of(
                                        new Climbable(
                                                AE.location("climbable_enchantment.ceiling"),
                                                BlockPredicate.allOf(
                                                        BlockPredicate.not(BlockPredicate.matchesTag(BlockTags.AIR)),
                                                        BlockPredicate.solid(),
                                                        BlockPredicate.noFluid(),
                                                        BlockPredicate.not(BlockPredicate.matchesTag(AEBlockTags.SLIPPERY))
                                                ),
                                                true,
                                                true
                                        )
                                ), MinMaxBounds.Ints.between(3, 3))
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
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.treasures"),
                                                Either.left(BlockVision.SpecialBlockType.TREASURES),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                10,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#a87c1a"),
                                                        Color.of(ChatFormatting.GOLD),
                                                        Color.of("#ff9f1a"),
                                                        Color.of("#ff5fd2"),
                                                        Color.of("#5fd9ff"),
                                                        Color.of("#ffffff")
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.atLeast(1)),
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.copper"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_COPPER)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#7a4a2e", 0.15f),
                                                        Color.of(ChatFormatting.DARK_GREEN, 0.15f),
                                                        Color.of("#3f7f5f", 0.15f)
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.between(1, 1)),
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.iron"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_IRON)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.WHITE, 0.15f),
                                                        Color.of(ChatFormatting.GRAY, 0.15f),
                                                        Color.of(ChatFormatting.DARK_GRAY, 0.15f)
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.redstone"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_REDSTONE)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.DARK_RED, 0.15f),
                                                        Color.of(ChatFormatting.RED, 0.15f),
                                                        Color.of("#ff4d4d", 0.15f)
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.zinc"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(AEBlockTags.ZINC_ORES)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#9aa3ad", 0.15f),
                                                        Color.of("#b7c0c9", 0.15f),
                                                        Color.of("#d0d7df", 0.15f)
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.silver"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(AEBlockTags.SILVER_ORES)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.WHITE, 0.15f),
                                                        Color.of(ChatFormatting.GRAY, 0.15f),
                                                        Color.of("#dfe6ee", 0.15f)
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.between(1, 2)),
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.lapis"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_LAPIS)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.DARK_BLUE, 0.15f),
                                                        Color.of(ChatFormatting.BLUE, 0.15f),
                                                        Color.of("#4169e1", 0.15f)
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.gold"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_GOLD)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.GOLD, 0.15f),
                                                        Color.of(ChatFormatting.YELLOW, 0.15f),
                                                        Color.of("#ffdd55", 0.15f)
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.between(2, 3)),
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.emerald"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_EMERALD)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.DARK_GREEN, 0.15f),
                                                        Color.of(ChatFormatting.GREEN, 0.15f),
                                                        Color.of("#55ff88", 0.15f)
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.diamond"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_DIAMOND)),
                                                LevelBasedValue.constant(24),
                                                BlockVision.DisplayType.SIMPLE_SHADER,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#3fffdc", 0.15f),
                                                        Color.of(ChatFormatting.AQUA, 0.15f),
                                                        Color.of("#3fc5ff", 0.15f),
                                                        Color.of("#8b7dff", 0.15f),
                                                        Color.of("#55ff88", 0.15f)
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.between(3, 3)),
                                new LevelBasedBlockVision.Entry(List.of(
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.emerald"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_EMERALD)),
                                                LevelBasedValue.constant(16),
                                                BlockVision.DisplayType.OUTLINE,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of(ChatFormatting.DARK_GREEN),
                                                        Color.of(ChatFormatting.GREEN),
                                                        Color.of("#55ff88")
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.diamond"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_DIAMOND)),
                                                LevelBasedValue.constant(16),
                                                BlockVision.DisplayType.OUTLINE,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#3fffdc"),
                                                        Color.of(ChatFormatting.AQUA),
                                                        Color.of("#3fc5ff"),
                                                        Color.of("#8b7dff"),
                                                        Color.of("#55ff88")
                                                ), 1, 1)
                                        ),
                                        new BlockVision(
                                                AE.location("block_vision_enchantment.netherite"),
                                                Either.right(context.lookup(Registries.BLOCK).getOrThrow(Tags.Blocks.ORES_NETHERITE_SCRAP)),
                                                LevelBasedValue.constant(16),
                                                BlockVision.DisplayType.OUTLINE,
                                                0,
                                                ShiftingColor.of(List.of(
                                                        Color.of("#8a502b"),
                                                        Color.of("#8a312c"),
                                                        Color.of("#852747")
                                                ), 1, 1)
                                        )
                                ), MinMaxBounds.Ints.atLeast(4))
                        )
                ).build()));
    }

    private static ResourceKey<Enchantment> key(final String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, AE.location(path));
    }
}
