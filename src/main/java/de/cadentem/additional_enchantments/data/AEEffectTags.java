package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class AEEffectTags extends ForgeRegistryTagsProvider<MobEffect> {
    public static final TagKey<MobEffect> TIPPED_BLACKLIST = new TagKey<>(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(AE.MODID, "tipped_blacklist"));

    public AEEffectTags(final DataGenerator generator, @Nullable final ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.MOB_EFFECTS, AE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(TIPPED_BLACKLIST);
    }
}