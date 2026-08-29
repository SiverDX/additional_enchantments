package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum AimPoint implements StringRepresentable {
    CENTER("center", target -> target.getBoundingBox().getCenter()),
    EYE("eye", Entity::getEyePosition),
    BOTTOM("bottom", target -> target.position().add(0, /* To avoid false-positive for BlockHitResult */ 0.1, 0));

    public static final Codec<AimPoint> CODEC = StringRepresentable.fromEnum(AimPoint::values);

    private final String name;
    private final Function<Entity, Vec3> positionSupplier;

    AimPoint(final String name, final Function<Entity, Vec3> positionSupplier) {
        this.name = name;
        this.positionSupplier = positionSupplier;
    }

    public static @Nullable AimPoint findFreePoint(final Projectile projectile, final Entity target) {
        for (AimPoint point : AimPoint.values()) {
            if (point.isFree(projectile, point.getPosition(target))) {
                return point;
            }
        }

        return null;
    }

    public Vec3 getPosition(final Entity target) {
        return positionSupplier.apply(target);
    }

    private boolean isFree(final Projectile projectile, final Vec3 position) {
        BlockHitResult hit = projectile.level().clip(new ClipContext(projectile.position(), position, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, projectile));
        return hit.getType() == HitResult.Type.MISS;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
