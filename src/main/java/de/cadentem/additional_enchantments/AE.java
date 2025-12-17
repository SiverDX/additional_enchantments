package de.cadentem.additional_enchantments;

import com.mojang.logging.LogUtils;
import de.cadentem.additional_enchantments.capability.PlayerData;
import de.cadentem.additional_enchantments.capability.ProjectileData;
import de.cadentem.additional_enchantments.client.BlockVisionShaderSimple;
import de.cadentem.additional_enchantments.client.ClientRegistry;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.config.VisionConfig;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import de.cadentem.additional_enchantments.registry.AEItems;
import de.cadentem.additional_enchantments.registry.AELootModifiers;
import de.cadentem.additional_enchantments.registry.AEMobEffects;
import de.cadentem.additional_enchantments.registry.AEParticles;
import de.cadentem.additional_enchantments.util.ClientProxy;
import de.cadentem.additional_enchantments.util.Proxy;
import de.cadentem.additional_enchantments.util.ServerProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(AE.MODID)
public class AE {
    public static final String MODID = "additional_enchantments";
    public static final Logger LOG = LogUtils.getLogger();

    public static Proxy PROXY;

    public AE() {
        PROXY = FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new ServerProxy();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        AEEnchantments.ENCHANTMENTS.register(modEventBus);
        AEEntityTypes.ENTITY_TYPES.register(modEventBus);
        AEItems.ITEMS.register(modEventBus);
        AELootModifiers.LOOT_MODIFIERS.register(modEventBus);
        AEMobEffects.MOB_EFFECTS.register(modEventBus);
        AEParticles.PARTICLE_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientRegistry::registerItemProperties);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(VisionConfig::updateFromConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BlockVisionShaderSimple::registerShaders);
        MinecraftForge.EVENT_BUS.addListener(VisionConfig::updateFromReload);
    }

    @SubscribeEvent
    public void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
        event.register(ProjectileData.class);
    }
}
