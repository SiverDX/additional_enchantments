package de.cadentem.additional_enchantments.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.BlockVisionData;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.mixin.RandomizableContainerBlockEntityAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "setupAndRender", at = @At("TAIL"))
    private static <T extends BlockEntity> void test(final BlockEntityRenderer<T> renderer, final T blockEntity, final float partialTick, final PoseStack pose, final MultiBufferSource source, final CallbackInfo callback) {
        if (blockEntity instanceof RandomizableContainerBlockEntityAccess access && access.additional_enchantments$getLootTable() != null) {
//            BlockState state = blockEntity.getBlockState();
//
//            if (!state.is(AEBlockTags.TREASURES)) {
//                return;
//            }
//
//            //noinspection DataFlowIssue -> player is present
//            BlockVisionData vision = Minecraft.getInstance().player.getExistingData(AEDataAttachments.BLOCK_VISION).orElse(null);
//
//            if (vision == null) {
//                return;
//            }
//
//            if (vision.getRange(state.getBlock()) == 0 || vision.getDisplayType(state.getBlock()) != VisionConfig.DisplayType.X_RAY_OUTLINE) {
//                return;
//            }
//
//            VertexConsumer buffer = source.getBuffer(RenderType.lines());
//            int color = ColorUtils.lerpColor(visionData.colorsARGB(), visionData.colorShiftRate(), 0);
//            // Block entities are rendered after blocks, making the lines disappear if they are withing the rendered block entity
//            // Therefor render them immediately after the block entity itself
//            VisionHandler.drawLines(buffer, pose.last(), 0, 0, 0, 1, 1, 1, color);
        }
    }
}
