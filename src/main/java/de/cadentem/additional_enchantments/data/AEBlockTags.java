package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> TREASURES = TagKey.create(Registries.BLOCK, AE.location("treasures"));
    public static final TagKey<Block> SLIPPERY = TagKey.create(Registries.BLOCK, AE.location("slippery"));
    public static final TagKey<Block> MISC_ORES = TagKey.create(Registries.BLOCK, AE.location("misc_ores"));

    // Compatibility
    public static final TagKey<Block> ZINC_ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/zinc"));
    public static final TagKey<Block> SILVER_ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/silver"));

    public AEBlockTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, AE.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(TREASURES).addTag(Tags.Blocks.CHESTS);

        tag(SLIPPERY)
                .addTag(BlockTags.ICE)
                .addTag(Tags.Blocks.GLASS_BLOCKS)
                .addTag(Tags.Blocks.GLASS_PANES);

        // Needed since missing block tags in predicates cause data errors now
        tag(ZINC_ORES);
        tag(SILVER_ORES);

        tag(MISC_ORES)
                .addTag(Tags.Blocks.ORES)
                // Not worth displaying
                .remove(Tags.Blocks.ORES_COAL)
                // Have specific entries
                .remove(Tags.Blocks.ORES_COPPER)
                .remove(Tags.Blocks.ORES_IRON)
                .remove(Tags.Blocks.ORES_REDSTONE)
                .remove(ZINC_ORES)
                .remove(SILVER_ORES)
                .remove(Tags.Blocks.ORES_LAPIS)
                .remove(Tags.Blocks.ORES_GOLD)
                .remove(Tags.Blocks.ORES_EMERALD)
                .remove(Tags.Blocks.ORES_DIAMOND)
                .remove(Tags.Blocks.ORES_NETHERITE_SCRAP)
        ;
    }
}
