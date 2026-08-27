package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AEFluidTypesTags extends TagsProvider<FluidType> {
    public static final TagKey<FluidType> BUMBLEZONE = TagKey.create(NeoForgeRegistries.FLUID_TYPES.key(), AE.location("bumblezone"));
    public static final TagKey<FluidType> CREATE = TagKey.create(NeoForgeRegistries.FLUID_TYPES.key(), AE.location("create"));

    protected AEFluidTypesTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
        super(output, NeoForgeRegistries.FLUID_TYPES.key(), provider, AE.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(BUMBLEZONE)
                .addOptional(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "sugar_water"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "honey"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "royal_jelly"))
        ;

        tag(CREATE)
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "honey"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "chocolate"))
        ;
    }
}
