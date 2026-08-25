package de.cadentem.additional_enchantments.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.common.network.SyncClimbFlag;
import de.cadentem.additional_enchantments.enchantments.climbing.CeilingClimbDimensions;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "setupRotations", at = @At("TAIL"))
    private void additional_enchantments$hangOnCeiling(final LivingEntity entity, final PoseStack poseStack, final float bob, final float yBodyRot, final float partialTick, final float scale, final CallbackInfo callback) {
        if (entity == null) {
            return;
        }

        ClimbableData data = entity.getExistingData(AEDataAttachments.CLIMBABLE).orElse(null);

        if (data == null || !(data.getClimbingType() == SyncClimbFlag.ClimbingType.CEILING || data.isCeilingClimbing())) {
            return;
        }

        // The hitbox is usually re-sized to the bottom part - but to keep to the ceiling we need to inverse that behaviour
        // Which also means we need to move the model up to be at the hitbox again
        double unmodifiedHeight = CeilingClimbDimensions.getUnmodifiedHeight(entity);

        // Due to the rotation, the model has basically "fallen over" so we move it back to keep the head at the hitbox
        poseStack.translate(0, unmodifiedHeight, unmodifiedHeight);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        // Need to invert the facing direction for movement since the model is inverted
        poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
    }
}
