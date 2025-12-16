package de.cadentem.additional_enchantments.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
    @Accessor("TRANSLUCENT_TRANSPARENCY")
    static RenderStateShard.TransparencyStateShard additional_enchantments$getTranslucentTransparency() {
        throw new AssertionError();
    }

    @Accessor("LEQUAL_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard additional_enchantments$getLEqualDepthTest() {
        throw new AssertionError();
    }

    @Accessor("COLOR_WRITE")
    static RenderStateShard.WriteMaskStateShard additional_enchantments$getColorWrite() {
        throw new AssertionError();
    }
}
