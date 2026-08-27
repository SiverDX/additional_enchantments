package de.cadentem.additional_enchantments.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow @Final private static int WATER_VISION_MAX_TIME;

    /** Immediate full visibility in water when water vision is active (+ relevant for brightness increase) */
    @ModifyExpressionValue(method = "getWaterVision", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/player/LocalPlayer;waterVisionTime:I", ordinal = 0))
    private int additional_enchantments$handleWaterVision(int original) {
        if (original == 0) {
            return 0;
        }

        LocalPlayer self = (LocalPlayer) (Object) this;

        boolean hasVision = self.getExistingData(AEDataAttachments.FLUID_VISION)
                .map(vision -> vision.get(NeoForgeMod.WATER_TYPE) != FluidVision.Mapped.NONE)
                .orElse(false);

        if (hasVision) {
            return WATER_VISION_MAX_TIME;
        }

        return original;
    }
}
