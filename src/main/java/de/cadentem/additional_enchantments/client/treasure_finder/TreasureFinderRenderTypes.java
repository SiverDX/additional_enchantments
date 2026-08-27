package de.cadentem.additional_enchantments.client.treasure_finder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

/**
 * This mostly exists due to the cutout render type, otherwise it will render full planes instead of the proper texture </br>
 * Only happens when 'Iris' is not present, causing the rendering pipeline to not apply the custom shader
 */
public final class TreasureFinderRenderTypes {
    private static RenderType CUTOUT;
    private static RenderType TRANSLUCENT;

    public static RenderType treasureFinderCutout() {
        if (CUTOUT == null) {
            //noinspection deprecation -> ignore
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(TreasureFinderHandler::getShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);

            CUTOUT = RenderType.create(
                    "additional_enchantments:treasure_finder_cutout",
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

    public static RenderType treasureFinderTranslucent() {
        if (TRANSLUCENT == null) {
            //noinspection deprecation -> ignore
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(TreasureFinderHandler::getShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);

            TRANSLUCENT = RenderType.create(
                    "additional_enchantments:treasure_finder_translucent",
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
