package de.cadentem.additional_enchantments.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.client.VisionHandler;
import de.cadentem.additional_enchantments.config.VisionConfig;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.util.ColorUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "setupAndRender", at = @At("TAIL"))
    private static <T extends BlockEntity> void test(final BlockEntityRenderer<T> renderer, final T block, final float partialTick, final PoseStack pose, final MultiBufferSource source, final CallbackInfo callback) {
        if (block.getBlockState().is(AEBlockTags.TREASURES) && block instanceof RandomizableContainerBlockEntityAccess access && access.additional_enchantments$getLootTable() != null) {
            VisionConfig.VisionData visionData = VisionConfig.SpecialBlock.TREASURE.get(4);

            if (visionData == null || visionData.displayType() != VisionConfig.DisplayType.X_RAY_OUTLINE) {
                return;
            }

            VertexConsumer buffer = source.getBuffer(RenderType.lines());
            int color = ColorUtils.lerpColor(visionData.colorsARGB(), visionData.colorShiftRate(), 0);
            // Block entities are rendered after blocks, making the lines disappear if they are withing the rendered block entity
            // Therefor render them immediately after the block entity itself
            VisionHandler.drawLines(buffer, pose.last(), 0, 0, 0, 1, 1, 1, color);
        }
    }
}
