package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEEffectTags extends TagsProvider<MobEffect> {
    public static final TagKey<MobEffect> TIPPED_BLACKLIST = TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(AE.MODID, "tipped_blacklist"));

    public AEEffectTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final ExistingFileHelper fileHelper) {
        super(output, Registries.MOB_EFFECT, lookupProvider, AE.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(TIPPED_BLACKLIST);
    }
}