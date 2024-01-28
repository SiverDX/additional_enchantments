package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.data.AEEntityTags;
import de.cadentem.additional_enchantments.enchantments.PerceptionEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void additional_enchantments$handlePerceptionEnchantment(final Entity entity, final CallbackInfoReturnable<Boolean> callback) {
        Player localPlayer = ClientProxy.getLocalPlayer();

        if (localPlayer == null) {
            return;
        }

        if (localPlayer == entity || entity.getType().is(AEEntityTags.PERCEPTION_BLACKLIST) || entity.isInvisible() && ServerConfig.SPEC.isLoaded() && !ServerConfig.PERCEPTION_SHOW_INVISIBLE.get()) {
            return;
        }

        PlayerDataProvider.getCapability(localPlayer).ifPresent(data -> {
            if (data.displayType == PerceptionEnchantment.DisplayType.NONE) {
                return;
            }

            if (entity instanceof ItemEntity item) {
                if (data.displayType == PerceptionEnchantment.DisplayType.NO_ITEMS) {
                    return;
                } else if (item.getItem().getRarity().ordinal() < data.itemFilter.ordinal()) {
                    return;
                }
            } else if (!(entity instanceof LivingEntity)) {
                return;
            }

            float distanceTo = localPlayer.distanceTo(entity);
            int level = PerceptionEnchantment.getClientEnchantmentLevel();

            if (level > 0 && distanceTo < level * 8) {
                callback.setReturnValue(true);
            }
        });
    }
}
