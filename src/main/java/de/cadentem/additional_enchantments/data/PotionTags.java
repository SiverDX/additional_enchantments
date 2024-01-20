package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PotionTags extends ForgeRegistryTagsProvider<Potion> {
    public static final TagKey<Potion> TIPPED_BLACKLIST = new TagKey<>(Registry.POTION_REGISTRY, new ResourceLocation(AE.MODID, "tipped_blacklist"));

    public PotionTags(final DataGenerator generator, @Nullable final ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.POTIONS, AE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(TIPPED_BLACKLIST);
    }
}
