package de.cadentem.additional_enchantments;

import com.mojang.logging.LogUtils;
import de.cadentem.additional_enchantments.capability.PlayerData;
import de.cadentem.additional_enchantments.capability.ProjectileData;
import de.cadentem.additional_enchantments.client.ClientRegistry;
import de.cadentem.additional_enchantments.config.ClientConfig;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.registry.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AE.MODID)
public class AE {
    public static final String MODID = "additional_enchantments";
    public static final Logger LOG = LogUtils.getLogger();

    public AE() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        AEEnchantments.ENCHANTMENTS.register(modEventBus);
        AEEntityTypes.ENTITY_TYPES.register(modEventBus);
        AEItems.ITEMS.register(modEventBus);
        AELootModifiers.LOOT_MODIFIERS.register(modEventBus);
        AEMobEffects.MOB_EFFECTS.register(modEventBus);
        AEParticles.PARTICLE_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientRegistry::registerItemProperties);
    }

    @SubscribeEvent
    public void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
        event.register(ProjectileData.class);
    }
}
