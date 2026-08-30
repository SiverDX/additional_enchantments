package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEItemTags extends ItemTagsProvider {
    public static final TagKey<Item> VALUABLES = TagKey.create(Registries.ITEM, AE.location("valuables"));
    public static final TagKey<Item> LIMITED_VALUABLES = TagKey.create(Registries.ITEM, AE.location("limited_valuables"));
    public static final TagKey<Item> HOMING_ENCHANTABLES = TagKey.create(Registries.ITEM, AE.location("homing_enchantables"));

    public AEItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, AE.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(LIMITED_VALUABLES)
                // Equipment
                .add(Items.NETHERITE_HELMET)
                .add(Items.NETHERITE_CHESTPLATE)
                .add(Items.NETHERITE_LEGGINGS)
                .add(Items.NETHERITE_BOOTS)
                .add(Items.NETHERITE_SWORD)
                .add(Items.NETHERITE_PICKAXE)
                .add(Items.NETHERITE_AXE)
                .add(Items.NETHERITE_SHOVEL)
                .add(Items.NETHERITE_HOE)
                .add(Items.ELYTRA)
                // Rare Consumables
                .add(Items.ENCHANTED_GOLDEN_APPLE)
                .add(Items.TOTEM_OF_UNDYING)
                // Rare Materials
                .add(Items.GOLD_BLOCK)
                .add(Items.DIAMOND)
                .add(Items.DIAMOND_BLOCK)
                .add(Items.EMERALD)
                .add(Items.EMERALD_BLOCK)
                .add(Items.ANCIENT_DEBRIS)
                .add(Items.NETHERITE_SCRAP)
                .add(Items.NETHERITE_INGOT)
                .add(Items.NETHERITE_BLOCK)
                // Trophies
                .add(Items.DRAGON_EGG)
                .add(Items.DRAGON_HEAD)
        ;

        tag(VALUABLES)
                // Misc.
                .addTag(ItemTags.TRIM_MATERIALS)
                .addTag(Tags.Items.MUSIC_DISCS)
                .addTag(Tags.Items.GEMS_DIAMOND)
                .addTag(AEItemTags.LIMITED_VALUABLES)
                // Equipment
                .add(Items.DIAMOND_HELMET)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.DIAMOND_SWORD)
                .add(Items.DIAMOND_PICKAXE)
                .add(Items.DIAMOND_AXE)
                .add(Items.DIAMOND_SHOVEL)
                .add(Items.DIAMOND_HOE)
                .add(Items.TRIDENT)
                .add(Items.MACE)
                // Rare Consumables
                .add(Items.GOLDEN_APPLE)
                // Rare Materials
                .add(Items.SHULKER_SHELL)
                .add(Items.ECHO_SHARD)
                .add(Items.HEART_OF_THE_SEA)
                .add(Items.NAUTILUS_SHELL)
                // Rare Items
                .add(Items.ENCHANTED_BOOK)
                .add(Items.HEAVY_CORE)
                .add(Items.BEACON)
                // Trophies
                .add(Items.WITHER_SKELETON_SKULL)
        ;

        tag(HOMING_ENCHANTABLES)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
                .addTag(ItemTags.TRIDENT_ENCHANTABLE)
        ;
    }
}
