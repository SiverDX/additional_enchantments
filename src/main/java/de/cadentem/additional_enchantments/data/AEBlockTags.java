package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class AEBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> COMMON_ORE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "common_ore"));
    public static final TagKey<Block> UNCOMMON_ORE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "uncommon_ore"));
    public static final TagKey<Block> RARE_ORE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "rare_ore"));
    public static final TagKey<Block> ORE_SIGHT_BLACKLIST = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "ore_sight_blacklist"));

    public AEBlockTags(final DataGenerator generator, @Nullable final ExistingFileHelper existingFileHelper) {
        super(generator, AE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(COMMON_ORE)
                .add(Blocks.COAL_ORE).add(Blocks.DEEPSLATE_COAL_ORE).addOptional(spelunkery("granite_coal_ore")).addOptional(spelunkery("andesite_coal_ore")).addOptional(spelunkery("andesite_coal_ore")).addOptional(spelunkery("diorite_coal_ore")).addOptional(spelunkery("tuff_coal_ore"))
                .add(Blocks.COPPER_ORE).add(Blocks.DEEPSLATE_COPPER_ORE).addOptional(spelunkery("granite_copper_ore")).addOptional(spelunkery("andesite_copper_ore")).addOptional(spelunkery("andesite_copper_ore")).addOptional(spelunkery("diorite_copper_ore")).addOptional(spelunkery("tuff_copper_ore"))
                .add(Blocks.IRON_ORE).add(Blocks.DEEPSLATE_IRON_ORE).addOptional(spelunkery("granite_iron_ore")).addOptional(spelunkery("andesite_iron_ore")).addOptional(spelunkery("andesite_iron_ore")).addOptional(spelunkery("diorite_iron_ore")).addOptional(spelunkery("tuff_iron_ore"))
                .add(Blocks.NETHER_GOLD_ORE)
                .add(Blocks.NETHER_QUARTZ_ORE);

        tag(UNCOMMON_ORE)
                .add(Blocks.GOLD_ORE).add(Blocks.DEEPSLATE_GOLD_ORE).addOptional(spelunkery("granite_gold_ore")).addOptional(spelunkery("andesite_gold_ore")).addOptional(spelunkery("andesite_gold_ore")).addOptional(spelunkery("diorite_gold_ore")).addOptional(spelunkery("tuff_gold_ore"))
                .add(Blocks.LAPIS_ORE).add(Blocks.DEEPSLATE_LAPIS_ORE).addOptional(spelunkery("granite_lapis_ore")).addOptional(spelunkery("andesite_lapis_ore")).addOptional(spelunkery("andesite_lapis_ore")).addOptional(spelunkery("diorite_lapis_ore")).addOptional(spelunkery("tuff_lapis_ore")).addOptional(spelunkery("sandstone_lapis_ore"))
                .add(Blocks.REDSTONE_ORE).add(Blocks.DEEPSLATE_REDSTONE_ORE).addOptional(spelunkery("granite_redstone_ore")).addOptional(spelunkery("andesite_redstone_ore")).addOptional(spelunkery("andesite_redstone_ore")).addOptional(spelunkery("diorite_redstone_ore")).addOptional(spelunkery("tuff_redstone_ore")).addOptional(spelunkery("calcite_redstone_ore"));

        tag(RARE_ORE)
                .add(Blocks.EMERALD_ORE).add(Blocks.DEEPSLATE_EMERALD_ORE).addOptional(spelunkery("granite_emerald_ore")).addOptional(spelunkery("andesite_emerald_ore")).addOptional(spelunkery("andesite_emerald_ore")).addOptional(spelunkery("diorite_emerald_ore")).addOptional(spelunkery("tuff_emerald_ore"))
                .add(Blocks.DIAMOND_ORE).add(Blocks.DEEPSLATE_DIAMOND_ORE).addOptional(spelunkery("granite_diamond_ore")).addOptional(spelunkery("andesite_diamond_ore")).addOptional(spelunkery("andesite_diamond_ore")).addOptional(spelunkery("diorite_diamond_ore")).addOptional(spelunkery("tuff_diamond_ore")).addOptional(spelunkery("smooth_basalt_diamond_ore"))
                .add(Blocks.ANCIENT_DEBRIS).addOptional(mod("irons_spellbooks", "arcane_debris"));
    }

    private ResourceLocation spelunkery(final String id) {
        return mod("spelunkery", id);
    }

    private ResourceLocation mod(final String modId, final String id) {
        return new ResourceLocation(modId, id);
    }
}