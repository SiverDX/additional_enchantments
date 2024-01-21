package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import de.cadentem.additional_enchantments.registry.AEItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        KeyHandler.CYCLE_TIPPED = new KeyMapping("keybind.additional_enchantments.cycle_tipped", GLFW.GLFW_KEY_G, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_TIPPED.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_TIPPED);

        KeyHandler.CYCLE_HOMING = new KeyMapping("keybind.additional_enchantments.cycle_homing", GLFW.GLFW_KEY_H, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_HOMING.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_HOMING);

        KeyHandler.CYCLE_EXPLOSIVE_TIP = new KeyMapping("keybind.additional_enchantments.cycle_explosive_tip", GLFW.GLFW_KEY_J, "keybind.additional_enchantments.category");
        KeyHandler.CYCLE_EXPLOSIVE_TIP.setKeyConflictContext(KeyConflictContext.IN_GAME);
        event.register(KeyHandler.CYCLE_EXPLOSIVE_TIP);
    }

    @SubscribeEvent
    public static void registerEntityRenderer(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AEEntityTypes.SHARD_ARROW.get(), ShardArrowRenderer::new);
    }

    public static void registerItemProperties() {
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation(AE.MODID, "shard"), (stack, level, livingEntity, seed) -> livingEntity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, AEItems.SHARD_ARROW.get()) ? 1 :0);
    }
}
