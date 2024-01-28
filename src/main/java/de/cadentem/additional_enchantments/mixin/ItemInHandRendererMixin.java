package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @ModifyExpressionValue(method = {"renderTwoHandedMap", "renderOneHandedMap"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInvisible()Z"))
    private boolean additional_enchantments$hunterLayer_map(boolean isInvisible) {
        return additional_enchantments$shouldRenderHunterLayer(isInvisible);
    }

    @ModifyExpressionValue(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z"))
    private boolean additional_enchantments$hunterLayer_emptyHand(boolean isInvisible) {
        return additional_enchantments$shouldRenderHunterLayer(isInvisible);
    }

    @Unique
    private boolean additional_enchantments$shouldRenderHunterLayer(boolean isInvisible) {
        AtomicBoolean result = new AtomicBoolean(isInvisible);

        if (isInvisible && HunterEnchantment.getClientEnchantmentLevel() > 0) {
            PlayerDataProvider.getCapability(ClientProxy.getLocalPlayer()).ifPresent(data -> {
                if (data.hunterStacks > 0) {
                    result.set(false);
                }
            });
        }

        return result.get();
    }
}
