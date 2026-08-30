package de.cadentem.additional_enchantments.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.cadentem.additional_enchantments.client.AERenderData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {
    protected LivingEntityRendererMixin(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "setupRotations", at = @At("TAIL"))
    private void additional_enchantments$hangOnCeiling(final S state, final PoseStack pose, final float rotation, final float scale, final CallbackInfo callback) {
        AERenderData.CeilingClimbingRenderData ceilingClimbing = state.getRenderDataOrDefault(AERenderData.CLIMBING, AERenderData.CeilingClimbingRenderData.NONE);

        if (ceilingClimbing == AERenderData.CeilingClimbingRenderData.NONE) {
            return;
        }

        // Due to the rotation, the model has basically "fallen over" so we move it back to keep the head at the hitbox
        pose.translate(0, ceilingClimbing.unmodifiedHeight(), ceilingClimbing.unmodifiedHeight());
        pose.mulPose(Axis.XP.rotationDegrees(90));
        // Need to invert the facing direction for movement since the model is inverted
        pose.mulPose(Axis.ZP.rotationDegrees(-180));
    }
}
