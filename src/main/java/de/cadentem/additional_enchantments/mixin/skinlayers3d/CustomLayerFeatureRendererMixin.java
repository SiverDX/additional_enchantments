package de.cadentem.additional_enchantments.mixin.skinlayers3d;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.HunterLayer;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import dev.tr7zw.skinlayers.renderlayers.CustomLayerFeatureRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(CustomLayerFeatureRenderer.class)
public abstract class CustomLayerFeatureRendererMixin {
    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z"))
    private boolean additional_enchantments$allowRenderWhenTransparent(boolean isInvisible, @Local(argsOnly = true) final AbstractClientPlayer player) {
        AtomicBoolean result = new AtomicBoolean(isInvisible);

        if (isInvisible && HunterEnchantment.getClientEnchantmentLevel(player) > 0 && !player.hasEffect(MobEffects.INVISIBILITY)) {
            PlayerDataProvider.getCapability(player).ifPresent(data -> {
                if (data.hasHunterStacks()) {
                    result.set(false);
                }
            });
        }

        return result.get();
    }

    @ModifyConstant(method = "renderLayers", constant = @Constant(intValue = -1), remap = false)
    private int additional_enchantments$modifyAlpha(int color, @Local(argsOnly = true) final AbstractClientPlayer player) {
        AtomicInteger result = new AtomicInteger(color);

        if (player.isInvisible() && !player.hasEffect(MobEffects.INVISIBILITY)) {
            int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel(player);

            if (enchantmentLevel > 0) {
                PlayerDataProvider.getCapability(player).ifPresent(data -> {
                    if (data.hasHunterStacks()) {
                        int alpha = (int) (HunterLayer.getAlpha(data.getHunterStacks(), enchantmentLevel) * 255) << 24;
                        int newColor = color & 0x00FFFFFF; // remove alpha
                        newColor = alpha | newColor; // set new alpha
                        result.set(newColor);
                    }
                });
            }
        }

        return result.intValue();
    }
}
