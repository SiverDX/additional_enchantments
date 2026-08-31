package de.cadentem.additional_enchantments.client.fluid_vision;

import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.FluidVisionData;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import de.cadentem.additional_enchantments.mixin.client.FluidRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
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

        // Means how see-through the fluid should be, meaning a value of 0 should give full visibility inside fluids
        float percent = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(data -> data.get(fluid, player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(percent, 1) == 0) {
            return;
        }

        float nearPlane = event.getNearPlaneDistance();

        if (nearPlane < 0) {
            nearPlane *= (1 + percent);
        } else {
            nearPlane *= percent;
        }

        event.setNearPlaneDistance(nearPlane);


        float farPlane = event.getFarPlaneDistance();
        // Minecraft seems to use 96 as some sort of max., see AtmosphericFogEnvironment
        float fluidVisibility = 96 * (1 - percent);

        if (farPlane > fluidVisibility) {
            return;
        }

        event.setFarPlaneDistance(fluidVisibility);
    }

    @SubscribeEvent
    public static void renderFireOverlay(final RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.fireImmune() && !player.isCreative() && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return;
        }

        boolean hasVision = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(data -> data.get(player.getFirstEyeInFluidType(), player.registryAccess()) != FluidVision.Mapped.NONE)
                .orElse(false);

        if (hasVision) {
            event.setCanceled(true);
        }
    }
}
