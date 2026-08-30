package de.cadentem.additional_enchantments.client.treasure_finder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/** Since block entity models have to quads to be collected, a manual rendering is needed */
public final class TreasureFinderShaderSimpleBlockEntities {
    private static final Identifier FLAT_TEXTURE = AE.location("textures/white.png");
    private static final List<AABB> FALLBACK = List.of(new AABB(0, 0, 0, 1, 1, 1));

    private static final RenderType TREASURE_FINDER_SHADER_TYPE = RenderType.create(
            "treasure_finder_shader_block_entity",
            RenderSetup.builder(TreasureFinderShaderSimple.pipeline())
                    .withTexture("Sampler0", FLAT_TEXTURE)
                    .createRenderSetup()
    );

    public static void render(final TreasureFinderHandler.Data data, final PoseStack pose, final VertexConsumer buffer, final int colorARGB) {
        BlockPos position = BlockPos.containing(data.x(), data.y(), data.z());

        //noinspection DataFlowIssue -> level is present
        VoxelShape shape = data.state().getShape(Minecraft.getInstance().level, position);
        List<AABB> boxes = shape.isEmpty() ? FALLBACK : shape.toAabbs();

        pose.pushPose();
        pose.translate(data.x(), data.y(), data.z());
        // Apply the randomized offset some blocks can have to their position
        Vec3 offset = data.state().getOffset(position);
        pose.translate(offset.x, offset.y, offset.z);

        PoseStack.Pose lastPose = pose.last();

        for (AABB box : boxes) {
            render(lastPose, buffer, box, colorARGB);
        }

        pose.popPose();
    }

    private static void render(final PoseStack.Pose pose, final VertexConsumer buffer, final AABB box, final int colorARGB) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // The faces are wound counter-clockwise (when viewed from the outside) since the pipeline culls back faces
        // Otherwise the (translucent) color would be blended twice for every box

        // Front (Z+)
        quad(pose, buffer, colorARGB, 0, 0, 1, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ);
        // Back (Z-)
        quad(pose, buffer, colorARGB, 0, 0, -1, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ);
        // Left (X-)
        quad(pose, buffer, colorARGB, -1, 0, 0, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ);
        // Right (X+)
        quad(pose, buffer, colorARGB, 1, 0, 0, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ);
        // Top (Y+)
        quad(pose, buffer, colorARGB, 0, 1, 0, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ);
        // Bottom (Y-)
        quad(pose, buffer, colorARGB, 0, -1, 0, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ);
    }

    private static void quad(
            final PoseStack.Pose pose, final VertexConsumer buffer, final int colorARGB,
            final float normalX, final float normalY, final float normalZ,
            final float x0, final float y0, final float z0,
            final float x1, final float y1, final float z1,
            final float x2, final float y2, final float z2,
            final float x3, final float y3, final float z3
    ) {
        vertex(pose, buffer, colorARGB, normalX, normalY, normalZ, x0, y0, z0, 0, 0);
        vertex(pose, buffer, colorARGB, normalX, normalY, normalZ, x1, y1, z1, 1, 0);
        vertex(pose, buffer, colorARGB, normalX, normalY, normalZ, x2, y2, z2, 1, 1);
        vertex(pose, buffer, colorARGB, normalX, normalY, normalZ, x3, y3, z3, 0, 1);
    }

    private static void vertex(
            final PoseStack.Pose pose, final VertexConsumer buffer, final int colorARGB,
            final float normalX, final float normalY, final float normalZ,
            final float x, final float y, final float z,
            final float u, final float v
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(colorARGB)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    public static RenderType renderType() {
        return TREASURE_FINDER_SHADER_TYPE;
    }
}
