package de.cadentem.additional_enchantments.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.common.network.SyncClimbFlag;
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

        ClimbableData data = entity.getExistingData(AEDataAttachments.CLIMBABLE_DATA).orElse(null);

        if (data == null || !(data.getClimbingType() == SyncClimbFlag.ClimbingType.CEILING || data.isCeilingClimbing())) {
            return;
        }

        ClientProxy.test(entity, poseStack);
    }
}
