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
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DataGeneration {
    @SubscribeEvent
    public static void generateServer(final GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        // Handle new enchantments
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(Registries.ENCHANTMENT, AEEnchantments::bootstrap);
        DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(output, provider, builder, Set.of(AE.MODID));
        generator.addProvider(true, datapackProvider);

        // Update the lookup provider with the new entries
        provider = datapackProvider.getRegistryProvider();

        generator.addProvider(true, new AEBlockTags(output, provider));
        generator.addProvider(true, new AEItemTags(output, provider));
        generator.addProvider(true, new AEFluidTypesTags(output, provider));
        generator.addProvider(true, new AEEnchantmentTags(output, provider));
    }
}
