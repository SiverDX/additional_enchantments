package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.core.interfaces.LivingEntityAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityAccess {
    @Unique
    private boolean additional_enchantments$wasInvisibilityModified;

    @Override
    public boolean additional_enchantments$wasInvisibilityModified() {
        return additional_enchantments$wasInvisibilityModified;
    }

    @Override
    public void additional_enchantments$setWasInvisibilityModified(boolean wasInvisibilityModified) {
        this.additional_enchantments$wasInvisibilityModified = wasInvisibilityModified;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveAdditionalData(final CompoundTag tag, final CallbackInfo callback) {
        tag.putBoolean("wasInvisibilityModified", additional_enchantments$wasInvisibilityModified);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readAdditionalData(final CompoundTag tag, final CallbackInfo callback) {
        additional_enchantments$wasInvisibilityModified = tag.getBoolean("wasInvisibilityModified");
    }
}
