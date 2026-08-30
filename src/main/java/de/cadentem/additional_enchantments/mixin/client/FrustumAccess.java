package de.cadentem.additional_enchantments.mixin.client;

import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface FrustumAccess {
    /** Skip the need to create an AABB */
    @Invoker("cubeInFrustum")
    int additional_enchantments$cubeInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
}
