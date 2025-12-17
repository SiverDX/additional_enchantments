package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.cadentem.additional_enchantments.mixin.client.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

/**
 * This mostly exists due to the cutout render type, otherwise it will render full planes instead of the proper texture </br>
 * (Only happens when 'Iris' is not present - likely due to its heavy adjustments to the MC rendering pipeline) </br>
 * If we just use the render type as-is, it will not make use of our custom shader
 */
public final class BlockVisionRenderTypes {
    private static RenderType CUTOUT;
    private static RenderType TRANSLUCENT;

    private BlockVisionRenderTypes() {}

    public static RenderType blockVisionCutout() {
        if (CUTOUT == null) {
            //noinspection deprecation -> ignore
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(VisionHandler::getSimpleShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setTransparencyState(RenderStateShardAccessor.additional_enchantments$getTranslucentTransparency())
                    .setDepthTestState(RenderStateShardAccessor.additional_enchantments$getLEqualDepthTest())
                    .setCullState(RenderStateShardAccessor.additional_enchantments$getNoCull())
                    .setWriteMaskState(RenderStateShardAccessor.additional_enchantments$getColorWrite())
                    .createCompositeState(true);

            CUTOUT = RenderType.create(
                    "additional_enchantments:block_vision_cutout",
                    DefaultVertexFormat.BLOCK,
                    VertexFormat.Mode.QUADS,
                    4194304, // Size of RenderType#SOLID
                    false,
                    true,
                    state
            );
        }

        return CUTOUT;
    }

    public static RenderType blockVisionTranslucent() {
        if (TRANSLUCENT == null) {
            //noinspection deprecation -> ignore
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(VisionHandler::getSimpleShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setTransparencyState(RenderStateShardAccessor.additional_enchantments$getTranslucentTransparency())
                    .setDepthTestState(RenderStateShardAccessor.additional_enchantments$getLEqualDepthTest())
                    .setCullState(RenderStateShardAccessor.additional_enchantments$getNoCull())
                    .setWriteMaskState(RenderStateShardAccessor.additional_enchantments$getColorWrite())
                    .createCompositeState(true);

            TRANSLUCENT = RenderType.create(
                    "additional_enchantments:block_vision_translucent",
                    DefaultVertexFormat.BLOCK,
                    VertexFormat.Mode.QUADS,
                    786432, // Size of RenderType#CUTOUT
                    false,
                    true,
                    state
            );
        }

        return TRANSLUCENT;
    }
}
