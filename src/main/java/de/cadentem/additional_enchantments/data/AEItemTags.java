package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.registry.AEItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AEItemTags extends ItemTagsProvider {
    public AEItemTags(final DataGenerator generator, final BlockTagsProvider provider, final ExistingFileHelper fileHelper) {
        super(generator, provider, AE.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(ItemTags.ARROWS).add(AEItems.SHARD_ARROW.get());
    }
}
