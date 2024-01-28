package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.client.HunterLayer;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(final EntityRendererProvider.Context context, final PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void additional_enchantments$registerRenderer(final EntityRendererProvider.Context context, boolean useSlimModel, final CallbackInfo callback) {
        addLayer(new HunterLayer(this));
    }

    @WrapOperation(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", ordinal = 0))
    private void additional_enchantments$handleTransparency(final ModelPart instance, final PoseStack poseStack, final VertexConsumer vertexConsumer, int packedLight, int packedOverlay, final Operation<Void> original, /* Method arguments */ final PoseStack methodPoseStack, final MultiBufferSource bufferSource, int combinedLight, final AbstractClientPlayer player) {
        int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel();
        AtomicBoolean wasRendered = new AtomicBoolean(false);

        if (enchantmentLevel > 0) {
            PlayerDataProvider.getCapability(ClientProxy.getLocalPlayer()).ifPresent(data -> {
                if (data.hunterStacks > 0) {
                    instance.visible = true;
                    instance.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation())), packedLight, packedOverlay, 1, 1, 1, HunterLayer.getAlpha(data.hunterStacks, enchantmentLevel));
                    wasRendered.set(true);
                }
            });
        }

        if (!wasRendered.get()) {
            original.call(instance, poseStack, vertexConsumer, packedOverlay, packedOverlay);
        }
    }
}
