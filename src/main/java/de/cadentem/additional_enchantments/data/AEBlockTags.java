package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AEBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> HUNTER_RELEVANT = TagKey.create(Registries.BLOCK, AE.location("hunter_relevant"));
    public static final TagKey<Block> BRACEWALK = TagKey.create(Registries.BLOCK, AE.location("bracewalk"));
    public static final TagKey<Block> TREASURES = TagKey.create(Registries.BLOCK, AE.location("treasures"));

    public AEBlockTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
        super(output, provider, AE.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(HUNTER_RELEVANT)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.SAPLINGS)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.LEAVES)
                .add(Blocks.GRASS_BLOCK)
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
                .addOptional(ResourceLocation.fromNamespaceAndPath("projectvibrantjourneys", "prickly_bush"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("vinery", "taiga_grape_bush_red"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("vinery", "taiga_grape_bush_white"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("iceandfire", "dragon_ice_spikes"));

        List<String> bushes = List.of(
                "blackberry",
                "blueberry",
                "cranberry",
                "raspberry"
        );

        for (String bush : bushes) {
            for (int stage = 0; stage < 4; stage++) {
                tag(BRACEWALK).addOptional(ResourceLocation.fromNamespaceAndPath("wildberries", bush + "_bush_stage_" + stage));
            }
        }

        tag(TREASURES).addTag(Tags.Blocks.CHESTS);
    }
}
