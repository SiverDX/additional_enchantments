package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.registry.AEItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AEItemTags extends ItemTagsProvider {

    public AEItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(ItemTags.ARROWS).add(AEItems.SHARD_ARROW.get());
    }
}
