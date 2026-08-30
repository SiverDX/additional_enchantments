package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEFluidTypesTags extends KeyTagProvider<FluidType> {
    public static final TagKey<FluidType> BUMBLEZONE = TagKey.create(NeoForgeRegistries.FLUID_TYPES.key(), AE.location("bumblezone"));
    public static final TagKey<FluidType> CREATE = TagKey.create(NeoForgeRegistries.FLUID_TYPES.key(), AE.location("create"));

    protected AEFluidTypesTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, NeoForgeRegistries.FLUID_TYPES.key(), provider, AE.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(BUMBLEZONE)
                .addOptional(key("the_bumblezone", "sugar_water"))
                .addOptional(key("the_bumblezone", "honey"))
                .addOptional(key("the_bumblezone", "royal_jelly"))
        ;

        tag(CREATE)
                .addOptional(key("create", "honey"))
                .addOptional(key("create", "chocolate"))
        ;
    }

    private static ResourceKey<FluidType> key(final String namespace, final String path) {
        return ResourceKey.create(NeoForgeRegistries.FLUID_TYPES.key(), Identifier.fromNamespaceAndPath(namespace, path));
    }
}
