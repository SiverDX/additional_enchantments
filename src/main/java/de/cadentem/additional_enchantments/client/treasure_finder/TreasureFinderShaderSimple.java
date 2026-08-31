package de.cadentem.additional_enchantments.client.treasure_finder;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.ArrayList;
import java.util.List;

public final class TreasureFinderShaderSimple {
    private static final Identifier TREASURE_FINDER_SHADER = AE.location("core/treasure_finder_simple");

    private static final RenderPipeline TREASURE_FINDER_SHADER_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(AE.location("pipeline/treasure_finder_shader"))
            .withVertexShader(TREASURE_FINDER_SHADER)
            .withFragmentShader(TREASURE_FINDER_SHADER)
            .withSampler("Sampler0") // Block texture atlas
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, /* It's an overlay */ false, /* Prevent z-fighting */ -1, -10))
            .build();

    private static final RenderType TREASURE_FINDER_SHADER_TYPE = RenderType.create(
            "treasure_finder_shader",
            RenderSetup.builder(TREASURE_FINDER_SHADER_PIPELINE)
                    .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                    .createRenderSetup()
    );

    public static void render(final TreasureFinderHandler.Data data, final PoseStack pose, final VertexConsumer buffer, final int colorARGB) {
        BlockPos position = BlockPos.containing(data.x(), data.y(), data.z());
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(data.state());

        List<BlockStateModelPart> parts = new ArrayList<>();
        //noinspection DataFlowIssue -> level is present
        model.collectParts(Minecraft.getInstance().level, position, data.state(), RandomSource.create(data.state().getSeed(position)), parts);

        pose.pushPose();
        pose.translate(data.x(), data.y(), data.z());
        // Apply the randomized offset some blocks can have to their position
        Vec3 offset = data.state().getOffset(position);
        pose.translate(offset.x, offset.y, offset.z);

        QuadInstance quadInstance = new QuadInstance();
        quadInstance.setColor(colorARGB);
        quadInstance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
        quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);

        PoseStack.Pose lastPose = pose.last();

        for (BlockStateModelPart part : parts) {
            // Unculled faces
            part.getQuads(null).forEach(quad -> buffer.putBakedQuad(lastPose, quad, quadInstance));

            // Culled faces
            for (Direction direction : Direction.values()) {
                part.getQuads(direction).forEach(quad -> buffer.putBakedQuad(lastPose, quad, quadInstance));
            }
        }

        pose.popPose();
    }

    public static RenderType renderType() {
        // TODO :: check if cutout blocks (e.g. plants) still need their own render type
        return TREASURE_FINDER_SHADER_TYPE;
    }

    public static RenderPipeline pipeline() {
        return TREASURE_FINDER_SHADER_PIPELINE;
    }

    public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        event.registerPipeline(TREASURE_FINDER_SHADER_PIPELINE);
    }
}
