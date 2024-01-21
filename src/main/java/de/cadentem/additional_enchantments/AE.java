package de.cadentem.additional_enchantments;

import com.mojang.logging.LogUtils;
import de.cadentem.additional_enchantments.capability.Configuration;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import de.cadentem.additional_enchantments.registry.AEItems;
import de.cadentem.additional_enchantments.registry.AEMobEffects;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
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
    public static final Logger LOGGER = LogUtils.getLogger();

    public AE() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        AEEnchantments.ENCHANTMENTS.register(modEventBus);
        AEMobEffects.MOB_EFFECTS.register(modEventBus);
        AEEntityTypes.ENTITY_TYPES.register(modEventBus);
        AEItems.ITEMS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(Items.CROSSBOW, new ResourceLocation(AE.MODID, "shard"), (stack, level, livingEntity, seed) -> livingEntity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, AEItems.SHARD_ARROW.get()) ? 1 :0));
    }

    @SubscribeEvent
    public void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(Configuration.class);
    }
}
