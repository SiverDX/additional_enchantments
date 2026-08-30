package de.cadentem.additional_enchantments.client.treasure_finder;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public class TreasureFinderOutline {
    private static final RenderPipeline TREASURE_FINDER_OUTLINE_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(AE.location("pipeline/treasure_finder_outline"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build();

    private static final RenderType TREASURE_FINDER_OUTLINE_TYPE = RenderType.create(
            "treasure_finder_outline",
            RenderSetup.builder(TREASURE_FINDER_OUTLINE_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup()
    );

    public static void render(final TreasureFinderHandler.Data data, final PoseStack pose, final VertexConsumer buffer, final int colorARGB) {
        // TODO :: check if there are ways to fix lines not being x-ray behind block entities if the block entity is rendered
        //         currently there are rendered after entities, so x-ray outlines will be visible through the previously rendered entities
        pose.pushPose();
        pose.translate(data.x(), data.y(), data.z());

        int alpha = Math.max(ARGB.alpha(colorARGB), 192);
        int visibleColor = ARGB.color(alpha, ARGB.red(colorARGB), ARGB.green(colorARGB), ARGB.blue(colorARGB));
        int shadowColor = ARGB.color(Math.max(alpha / 2, 96), 0, 0, 0);

        ShapeRenderer.renderShape(pose, buffer, Shapes.create(new AABB(0, 0, 0, 1, 1, 1)), 0, 0, 0, shadowColor, 4);
        ShapeRenderer.renderShape(pose, buffer, Shapes.create(new AABB(0, 0, 0, 1, 1, 1)), 0, 0, 0, visibleColor, 2);
        pose.popPose();
    }

    public static void registerRenderPipelines(final RegisterRenderPipelinesEvent event) {
        event.registerPipeline(TREASURE_FINDER_OUTLINE_PIPELINE);
    }

    public static RenderType renderType() {
        return TREASURE_FINDER_OUTLINE_TYPE;
    }
}
