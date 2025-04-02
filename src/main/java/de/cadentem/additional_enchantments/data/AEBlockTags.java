package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AEBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> HUNTER_RELEVANT = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "hunter_relevant"));
    public static final TagKey<Block> BRACEWALK = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "bracewalk"));
    public static final TagKey<Block> VOIDING = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "voiding"));
    public static final TagKey<Block> TREASURES = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(AE.MODID, "treasures"));

    public AEBlockTags(final DataGenerator generator, @Nullable final ExistingFileHelper fileHelper) {
        super(generator, AE.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(HUNTER_RELEVANT)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.SAPLINGS)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.LEAVES)
                .add(Blocks.GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.SEAGRASS)
                .add(Blocks.TALL_SEAGRASS)
                .add(Blocks.FERN)
                .add(Blocks.LARGE_FERN)
                .add(Blocks.DEAD_BUSH)
                .add(Blocks.SWEET_BERRY_BUSH)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.BIG_DRIPLEAF)
                .add(Blocks.SMALL_DRIPLEAF)
                .add(Blocks.NETHER_SPROUTS)
                .add(Blocks.WARPED_ROOTS)
                .add(Blocks.WARPED_NYLIUM)
                .add(Blocks.CRIMSON_NYLIUM);

        tag(BRACEWALK)
                .add(Blocks.SWEET_BERRY_BUSH)
                .add(Blocks.COBWEB)
                .addOptional(new ResourceLocation("projectvibrantjourneys", "prickly_bush"))
                .addOptional(new ResourceLocation("vinery", "taiga_grape_bush_red"))
                .addOptional(new ResourceLocation("vinery", "taiga_grape_bush_white"))
                .addOptional(new ResourceLocation("iceandfire", "dragon_ice_spikes"));

        List<String> bushes = List.of(
                "blackberry",
                "blueberry",
                "cranberry",
                "raspberry"
        );

        for (String bush : bushes) {
            for (int stage = 0; stage < 4; stage++) {
                tag(BRACEWALK).addOptional(new ResourceLocation("wildberries", bush + "_bush_stage_" + stage));
            }
        }

        tag(VOIDING)
                .addTag(BlockTags.DIRT)
                .addTag(Tags.Blocks.STONE)
                .addTag(Tags.Blocks.COBBLESTONE)
                .addTag(Tags.Blocks.SAND)
                .addTag(Tags.Blocks.GRAVEL)
                .addTag(Tags.Blocks.NETHERRACK);

        tag(TREASURES)
                .addTag(Tags.Blocks.CHESTS);
    }
}
