package de.cadentem.additional_enchantments.client.block_vision;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.client.VisionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ShaderSimpleBlockEntities {
    private static final ResourceLocation FLAT_TEXTURE = new ResourceLocation(AE.MODID, "textures/white.png");

    // No special handling needed here since we render a simple box
    private static BufferBuilder buffer;
    private static Tesselator tesselator;

    public static void render(final VisionHandler.Data data, final PoseStack pose) {
        prepare();

        int colorARGB = data.getColor();
        int alpha = FastColor.ARGB32.alpha(colorARGB);
        int red = FastColor.ARGB32.red(colorARGB);
        int green = FastColor.ARGB32.green(colorARGB);
        int blue = FastColor.ARGB32.blue(colorARGB);

        if (alpha == 0) {
            return;
        }

        BlockPos position = BlockPos.containing(data.x(), data.y(), data.z());
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);

        pose.pushPose();
        // Apply the randomized offset some blocks can have to their position
        Vec3 offset = data.state().getOffset(level, position);
        pose.translate(offset.x, offset.y, offset.z);
        PoseStack.Pose lastPose = pose.last();
        pose.popPose();

        // Block entities do not have any quad data in the baked model
        // 'IForgeBlockEntity#getRenderBoundingBox' does not match the actual shape
        // Since it is usually extended due to prevent too-early culling
        // AABB box = AABB.ofSize(new Vec3(data.x(), data.y(), data.z()), 1, 1, 1);
        AABB box = new AABB(data.x(), data.y(), data.z(), data.x() + 1, data.y() + 1, data.z() + 1);

        float minX = (float) (box.minX - data.x());
        float minY = (float) (box.minY - data.y());
        float minZ = (float) (box.minZ - data.z());
        float maxX = (float) (box.maxX - data.x());
        float maxY = (float) (box.maxY - data.y());
        float maxZ = (float) (box.maxZ - data.z());

        renderAabbQuads(lastPose, minX, minY, minZ, maxX, maxY, maxZ, red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    private static void renderAabbQuads(
            final PoseStack.Pose pose,
            final float minX, final float minY, final float minZ,
            final float maxX, final float maxY, final float maxZ,
            final float red, final float green, final float blue, final float alpha
    ) {
        // Front (Z+)
        quad(buffer, pose, minX, minY, maxZ,  maxX, minY, maxZ,  maxX, maxY, maxZ,  minX, maxY, maxZ, red, green, blue, alpha, 0, 0, 1);
        // Back (Z-)
        quad(buffer, pose, maxX, minY, minZ,  minX, minY, minZ,  minX, maxY, minZ,  maxX, maxY, minZ, red, green, blue, alpha, 0, 0, -1);
        // Left (X-)
        quad(buffer, pose, minX, minY, minZ,  minX, minY, maxZ,  minX, maxY, maxZ,  minX, maxY, minZ, red, green, blue, alpha, -1, 0, 0);
        // Right (X+)
        quad(buffer, pose, maxX, minY, maxZ,  maxX, minY, minZ,  maxX, maxY, minZ,  maxX, maxY, maxZ, red, green, blue, alpha, 1, 0, 0);
        // Top (Y+)
        quad(buffer, pose, minX, maxY, maxZ,  maxX, maxY, maxZ,  maxX, maxY, minZ,  minX, maxY, minZ, red, green, blue, alpha, 0, 1, 0);
        // Bottom (Y-)
        quad(buffer, pose, minX, minY, minZ,  maxX, minY, minZ,  maxX, minY, maxZ,  minX, minY, maxZ, red, green, blue, alpha, 0, -1, 0);
    }

    private static void quad(
            final VertexConsumer buf,
            final PoseStack.Pose pose,
            final float x1, final float y1, final float z1,
            final float x2, final float y2, final float z2,
            final float x3, final float y3, final float z3,
            final float x4, final float y4, final float z4,
            final float red, final float green, final float blue, final float alpha,
            final int nx, final int ny, final int nz
    ) {
        buf.vertex(pose.pose(), x1, y1, z1).color(red, green, blue, alpha).uv(0, 0).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY).normal(pose.normal(), nx, ny, nz).endVertex();
        buf.vertex(pose.pose(), x2, y2, z2).color(red, green, blue, alpha).uv(1, 0).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY).normal(pose.normal(), nx, ny, nz).endVertex();
        buf.vertex(pose.pose(), x3, y3, z3).color(red, green, blue, alpha).uv(1, 1).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY).normal(pose.normal(), nx, ny, nz).endVertex();
        buf.vertex(pose.pose(), x4, y4, z4).color(red, green, blue, alpha).uv(0, 1).uv2(LightTexture.FULL_BRIGHT).overlayCoords(OverlayTexture.NO_OVERLAY).normal(pose.normal(), nx, ny, nz).endVertex();
    }

    public static void beginBatch() {
        tesselator = RenderSystem.renderThreadTesselator();
        buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
    }

    public static void endBatch() {
        prepare();

        if (tesselator != null) {
            tesselator.end();
        }

        // Revert back to default values
        RenderSystem.depthMask(true);
        RenderSystem.polygonOffset(0, 0);
        RenderSystem.disablePolygonOffset();

        VisionHandler.getSimpleShader().clear();

        tesselator = null;
        buffer = null;
    }

    @SuppressWarnings("DataFlowIssue") // Shader variables should be present
    private static void prepare() {
        RenderSystem.setShader(VisionHandler::getSimpleShader);
        VisionHandler.getSimpleShader().getUniform("ProjMat").set(RenderSystem.getProjectionMatrix());
        VisionHandler.getSimpleShader().getUniform("ModelViewMat").set(RenderSystem.getModelViewMatrix());
        VisionHandler.getSimpleShader().getUniform("DepthBias").set(0.0115f);
        VisionHandler.getSimpleShader().apply();

        if (tesselator != null) {
            RenderSystem.enableDepthTest();
            // Emulate vanilla cutout state: no blending and write depth
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            // Don't render both sides of transparent blocks (like plants)
            RenderSystem.enableCull();
            RenderSystem.enablePolygonOffset();
            // Prevents z-fighting issues
            RenderSystem.polygonOffset(-1, -1);
        }

        RenderSystem.setShaderTexture(0, FLAT_TEXTURE);
    }
}
