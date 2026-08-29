package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
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

    public @Nullable Vec3 findPosition(final Entity target) {
        return positionSupplier.apply(target);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
