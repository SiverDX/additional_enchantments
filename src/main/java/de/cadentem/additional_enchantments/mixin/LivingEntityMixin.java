package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.core.interfaces.LivingEntityAccess;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccess {
    @Unique
    public int additional_enchantments$trackedByCount;

    @Override
    public int additional_enchantments$getTrackedByCount() {
        return additional_enchantments$trackedByCount;
    }

    @Override
    public void additional_enchantments$addTracked() {
        additional_enchantments$trackedByCount++;
    }

    @Override
    public void additional_enchantments$removeTracked() {
        additional_enchantments$trackedByCount--;
    }
}
