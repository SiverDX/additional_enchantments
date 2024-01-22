package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import de.cadentem.additional_enchantments.enchantments.PerceptionEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 750)
public abstract class EntityMixin {
    /**
     * Clean up references to other objects
     */
    @Inject(method = "remove", at = @At("HEAD"))
    private void clearReferences(final Entity.RemovalReason reason, final CallbackInfo callback) {
        HomingEnchantment.clearHomingContext((Entity) (Object) this);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void handlePerceptionEnchantment(final CallbackInfoReturnable<Boolean> callback) {
        Player localPlayer = ClientProxy.getLocalPlayer();
        Entity instance = (Entity) (Object) this;

        if (localPlayer == instance || instance.getType().is(EntityTags.PERCEPTION_BLACKLIST) || instance.isInvisible() && ServerConfig.SPEC.isLoaded() && !ServerConfig.PERCEPTION_SHOW_INVISIBLE.get()) {
            return;
        }

        CapabilityProvider.getCapability(localPlayer).ifPresent(configuration -> {
            if (configuration.displayType == PerceptionEnchantment.DisplayType.NONE) {
                return;
            }

            if (instance instanceof ItemEntity item) {
                if (configuration.displayType == PerceptionEnchantment.DisplayType.NO_ITEMS) {
                    return;
                } else if (item.getItem().getRarity().ordinal() < configuration.itemFilter.ordinal()) {
                    return;
                }
            }

            float distanceTo = localPlayer.distanceTo(instance);
            int level = PerceptionEnchantment.getClientEnchantmentLevel();

            if (level > 0 && distanceTo < level * 8) {
                callback.setReturnValue(true);
            }
        });
    }
}
