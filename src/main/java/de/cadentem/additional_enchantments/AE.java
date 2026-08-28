package de.cadentem.additional_enchantments;

import com.mojang.logging.LogUtils;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.client.AEParticles;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.enchantments.AEEnchantmentRegistry;
import de.cadentem.additional_enchantments.server.ServerProxy;
import de.cadentem.additional_enchantments.server.conditions.AELootItemConditions;
import de.cadentem.additional_enchantments.util.Proxy;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(AE.MODID)
public class AE {
    public static final String MODID = "additional_enchantments";
    public static final Logger LOG = LogUtils.getLogger();

    public static Proxy PROXY;

    public AE(final IEventBus eventBus, final ModContainer container) {
        PROXY = FMLLoader.getDist().isClient() ? new ClientProxy() : new ServerProxy();

        AEDataAttachments.REGISTRY.register(eventBus);
        AELootItemConditions.REGISTRY.register(eventBus);
        AEEnchantmentRegistry.COMPONENT_REGISTRY.register(eventBus);
        AEEnchantmentRegistry.ENTITY_EFFECT_REGISTRY.register(eventBus);
        AEEnchantmentRegistry.LOCATION_EFFECT_REGISTRY.register(eventBus);
        AEParticles.REGISTRY.register(eventBus);
    }

    public static ResourceLocation location(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
