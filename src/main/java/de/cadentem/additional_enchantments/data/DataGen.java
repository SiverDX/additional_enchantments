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
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new AEBlockTags(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new AEEntityTags(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new AEEffectTags(generator, existingFileHelper));
    }
}
