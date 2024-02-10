package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import de.cadentem.additional_enchantments.registry.AEItems;
import de.cadentem.additional_enchantments.registry.AEParticles;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        KeyHandler.CYCLE_TIPPED = new KeyMapping("keybind.additional_enchantments.cycle_tipped", InputConstants.KEY_G, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_TIPPED.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_TIPPED);

        KeyHandler.CYCLE_HOMING = new KeyMapping("keybind.additional_enchantments.cycle_homing", InputConstants.KEY_H, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_HOMING.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_HOMING);

        KeyHandler.CYCLE_EXPLOSIVE_TIP = new KeyMapping("keybind.additional_enchantments.cycle_explosive_tip", InputConstants.KEY_J, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_EXPLOSIVE_TIP.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_EXPLOSIVE_TIP);

        KeyHandler.CYCLE_PERCEPTION = new KeyMapping("keybind.additional_enchantments.cycle_perception", InputConstants.KEY_U, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_PERCEPTION.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_PERCEPTION);

        KeyHandler.CYCLE_ORE_SIGHT = new KeyMapping("keybind.additional_enchantments.ore_sight", InputConstants.KEY_U, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_ORE_SIGHT.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_ORE_SIGHT);

        KeyHandler.CYCLE_VOIDING = new KeyMapping("keybind.additional_enchantments.voiding", InputConstants.KEY_G, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_VOIDING.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_VOIDING);
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.register(AEParticles.PLAGUE.get(), PlagueParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderer(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AEEntityTypes.SHARD_ARROW.get(), ShardArrowRenderer::new);
    }

    public static void registerItemProperties() {
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation(AE.MODID, "shard"), (stack, level, livingEntity, seed) -> livingEntity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, AEItems.SHARD_ARROW.get()) ? 1 : 0);
    }
}
