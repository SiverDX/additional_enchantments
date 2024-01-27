package de.cadentem.additional_enchantments.mixin.skinlayers3d;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.concurrent.atomic.AtomicBoolean;

@Debug(export = true)
@Pseudo // FIXME :: reverts the skin to default?
@Mixin(targets = "dev.tr7zw.skinlayers.renderlayers.CustomLayerFeatureRenderer")
public abstract class CustomLayerFeatureRendererMixin {
    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z"))
    private boolean additional_enchantments$allowRenderWhenTransparent(boolean isInvisible, @Local(argsOnly = true) final AbstractClientPlayer player) {
        AtomicBoolean result = new AtomicBoolean(isInvisible);

        if (isInvisible) {
            ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                if (configuration.hunterStacks > 0) {
                    result.set(false);
                }
            });
        }

        return result.get();
    }

    @ModifyConstant(method = "renderLayers(Lnet/minecraft/client/player/AbstractClientPlayer;Ldev/tr7zw/skinlayers/accessor/PlayerSettings;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", constant = @Constant(floatValue = 1.0f, ordinal = 3), remap = false)
    private float additional_enchantments$modifyAlpha(float alpha, @Local(argsOnly = true) final AbstractClientPlayer player) {
        AtomicDouble result = new AtomicDouble(alpha);

        if (player.isInvisible()) {
            ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
                if (configuration.hunterStacks > 0) {
                    int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel();

                    if (enchantmentLevel > 0) {
                        result.set(1f - (float) configuration.hunterStacks / HunterEnchantment.getMaxStacks(enchantmentLevel));
                    }
                }
            });
        }

        return result.floatValue();
    }
}
