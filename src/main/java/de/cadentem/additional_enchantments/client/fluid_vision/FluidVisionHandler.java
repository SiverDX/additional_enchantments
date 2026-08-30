package de.cadentem.additional_enchantments.client.fluid_vision;

import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.FluidVisionData;
import de.cadentem.additional_enchantments.mixin.client.FluidRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

@EventBusSubscriber(Dist.CLIENT)
public class FluidVisionHandler {
    /** The alpha change in {@link FluidRendererMixin} requires the drawn blocks to be uncached and be re-rendered */
    @SubscribeEvent
    public static void updateLevelRenderer(final RenderLevelStageEvent.AfterLevel event) {
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
        FluidType fluid = player.getFirstEyeInFluidType();

        if (fluid == NeoForgeMod.EMPTY_TYPE.value()) {
            return;
        }

        float multiplier = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(data -> data.get(fluid, player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(multiplier, 1) == 0) {
            return;
        }

        float nearPlane = event.getNearPlaneDistance();

        if (nearPlane < 0) {
            nearPlane *= (1 + multiplier);
        } else {
            nearPlane *= multiplier;
        }

        event.setNearPlaneDistance(nearPlane);

        float farPlane = event.getFarPlaneDistance();
        float fluidVisibility = Minecraft.getInstance().options.renderDistance().get().floatValue() * multiplier;

        if (farPlane > fluidVisibility) {
            return;
        }

        event.setFarPlaneDistance(fluidVisibility);
    }
}
