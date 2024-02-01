package de.cadentem.additional_enchantments.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void configureDataGen(final GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        AEBlockTags blockTagsProvider = new AEBlockTags(generator, fileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new AEItemTags(generator, blockTagsProvider, fileHelper));
        generator.addProvider(event.includeServer(), new AEEntityTags(generator, fileHelper));
        generator.addProvider(event.includeServer(), new AEEffectTags(generator, fileHelper));
        generator.addProvider(event.includeServer(), new AELootModifiers(generator));
    }
}
