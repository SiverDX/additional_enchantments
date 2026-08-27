package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.AEEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGeneration {
    @SubscribeEvent
    public static void generate(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        // Handle new enchantments
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(Registries.ENCHANTMENT, AEEnchantments::bootstrap);
        DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(output, provider, builder, Set.of(AE.MODID));
        generator.addProvider(event.includeServer(), datapackProvider);

        // Update the lookup provider with the new entries
        provider = datapackProvider.getRegistryProvider();

        AEBlockTags blockTags = new AEBlockTags(output, provider, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new AEItemTags(output, provider, blockTags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new AEFluidTypesTags(output, provider, helper));
    }
}
