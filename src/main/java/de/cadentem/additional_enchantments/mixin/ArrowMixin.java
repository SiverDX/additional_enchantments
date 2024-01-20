package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.core.interfaces.ArrowAccess;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Arrow.class)
public abstract class ArrowMixin implements ArrowAccess {
    @Unique // FIXME :: Needs to be persisted (otherwise they can be picked up as tipped arrows on server restart)
    private boolean additional_enchantments$hasModifiedEffects;

    @Override
    public void additional_enchantments$setHasModifiedEffect(boolean hasModifiedEffects) {
        this.additional_enchantments$hasModifiedEffects = hasModifiedEffects;
    }

    @Override
    public boolean additional_enchantments$hasEffect() {
        return potion != Potions.EMPTY || !effects.isEmpty();
    }

    @Inject(method = "getPickupItem", at = @At("HEAD"), cancellable = true)
    private void additional_enchantments$returnNormalArrow(final CallbackInfoReturnable<ItemStack> callback) {
        // TODO :: alternative -> set pickup to creative only
        if (additional_enchantments$hasModifiedEffects) {
            callback.setReturnValue(new ItemStack(Items.ARROW));
        }
    }

    @Shadow private Potion potion;
    @Shadow @Final private Set<MobEffectInstance> effects;
}
