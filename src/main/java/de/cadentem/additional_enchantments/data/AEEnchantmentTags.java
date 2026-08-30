package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.AEEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEEnchantmentTags extends EnchantmentTagsProvider {
    public static final TagKey<Enchantment> VISIONS = TagKey.create(Registries.ENCHANTMENT, AE.location("visions"));

    public AEEnchantmentTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, AE.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(VISIONS)
                .add(AEEnchantments.FLUID_VISION)
                .add(AEEnchantments.PERCEPTION)
                .add(AEEnchantments.TREASURE_FINDER);
    }
}
