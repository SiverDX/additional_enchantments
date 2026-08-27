package de.cadentem.additional_enchantments.mixin.resourcefullib;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.client.AlphaVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.teamresourceful.resourcefullib.common.fluid.neoforge.ResourcefulFluidType$1")
public abstract class MixinResourcefulFluidType {
    @ModifyArg(method = "renderFluid", at = @At(value = "INVOKE", target = "Lcom/teamresourceful/resourcefullib/client/fluid/data/ClientFluidProperties;renderFluid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Ljava/util/function/Function;)Z"))
    private VertexConsumer test(final VertexConsumer original, @Local(argsOnly = true) final FluidState fluid) {
        LocalPlayer player = Minecraft.getInstance().player;

        //noinspection DataFlowIssue -> player is present
        float percentage = player.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(fluid.getFluidType(), player.registryAccess()).percentage())
                .orElse(1f);

        if (Float.compare(percentage, 1) == 0) {
            return original;
        }

        return new AlphaVertexConsumer(original, percentage);
    }
}
