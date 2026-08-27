package de.cadentem.additional_enchantments.client.fluid_vision;

import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.FluidVisionData;
import de.cadentem.additional_enchantments.mixin.client.LiquidBlockRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

@EventBusSubscriber
public class FluidVisionHandler {
    /** The alpha change in {@link LiquidBlockRendererMixin} requires the drawn blocks to be uncached and be re-rendered */
    @SubscribeEvent
    public static void updateLevelRenderer(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        //noinspection DataFlowIssue -> player is not null
        FluidVisionData vision = Minecraft.getInstance().player.getExistingData(AEDataAttachments.FLUID_VISION).orElse(null);

        if (vision == null) {
            return;
        }

        if (vision.pendingVisionUpdate) {
            event.getLevelRenderer().allChanged();
            vision.pendingVisionUpdate = false;
        }
    }

    /** Clear up the visibility in dense fluids */
    @SubscribeEvent
    public static void onRenderFog(final ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;
        //noinspection DataFlowIssue -> player is present
        FluidType fluid = player.getEyeInFluidType();

        if (fluid == NeoForgeMod.EMPTY_TYPE.value()) {
            return;
        }

        float multiplier = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(data -> data.get(fluid, player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(multiplier, 1) != 0) {
            event.setNearPlaneDistance(event.getNearPlaneDistance() * multiplier);
            event.setFarPlaneDistance(event.getRenderer().getRenderDistance() * (1 - multiplier));
            event.setCanceled(true);
        }
    }
}
