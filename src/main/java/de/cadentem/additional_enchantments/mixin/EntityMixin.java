package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.additional_enchantments.util.IBoundingBoxOffset;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin implements IBoundingBoxOffset {
    /** To keep track of the current height modification (difference) */
    @Unique private double additional_enchantments$boundingBoxOffset;

    /**
     * When a hitbox shrinks, it usually "lowers", reducing the maxY </br>
     * However, to keep sticking on the ceiling, we need to "shrink" from the bottom, hence the offset
     */
    @ModifyReturnValue(method = "makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;", at = @At("RETURN"))
    private AABB additional_enchantments$anchorToCeiling(final AABB original) {
        if (additional_enchantments$boundingBoxOffset == 0) {
            return original;
        }

        return original.move(0, additional_enchantments$boundingBoxOffset, 0);
    }

    @Override
    public double additional_enchantments$getBoundingBoxOffset() {
        return additional_enchantments$boundingBoxOffset;
    }

    @Override
    public void additional_enchantments$setBoundingBoxOffset(final double offset) {
        additional_enchantments$boundingBoxOffset = offset;
    }
}
