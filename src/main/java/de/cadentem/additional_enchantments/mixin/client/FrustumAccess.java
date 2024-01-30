package de.cadentem.additional_enchantments.mixin.client;

import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface FrustumAccess {
    @Invoker("cubeInFrustum")
    boolean isCubeInFrustum(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax);
}
