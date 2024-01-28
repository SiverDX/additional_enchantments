package de.cadentem.additional_enchantments.mixin.skinlayers3d;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.HunterLayer;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(value = PlayerRenderer.class, priority = 1500)
public class PlayerRenderMixin {
    @TargetHandler(mixin = "dev.tr7zw.skinlayers.mixin.PlayerRendererMixin", name = "renderHand")
    @Redirect(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldev/tr7zw/skinlayers/api/Mesh;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void handleTransparecny(final Mesh instance, final PoseStack poseStack, final VertexConsumer vertexConsumer, int packedLight, int packedOverlay, /* Method arguments */ final PoseStack methodPoseStack, final MultiBufferSource bufferSource, int methodPackedLight, final AbstractClientPlayer player) {
        int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel();
        AtomicBoolean wasRendered = new AtomicBoolean(false);

        if (enchantmentLevel > 0) {
            PlayerDataProvider.getCapability(player).ifPresent(data -> {
                if (data.hunterStacks > 0) {
                    instance.setVisible(true);
                    instance.render(null, poseStack, vertexConsumer, packedLight, packedOverlay, 1, 1, 1, HunterLayer.getAlpha(data.hunterStacks, enchantmentLevel));
                    wasRendered.set(true);
                }
            });
        }

        if (!wasRendered.get()) {
            instance.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
    }
}
