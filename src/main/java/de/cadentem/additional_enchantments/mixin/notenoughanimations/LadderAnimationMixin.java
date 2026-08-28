package de.cadentem.additional_enchantments.mixin.notenoughanimations;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import dev.tr7zw.notenoughanimations.animations.fullbody.LadderAnimation;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LadderAnimation.class)
public abstract class LadderAnimationMixin {
    @ModifyExpressionValue(method = "isValid", at = @At(value = "INVOKE", target = "Ljava/lang/Class;isAssignableFrom(Ljava/lang/Class;)Z"))
    public boolean additional_enchantments$allowAnimation(final boolean isAssignable) {
        if (isAssignable) {
            return true;
        }

        //noinspection DataFlowIssue -> player is present
        return Minecraft.getInstance().player.getExistingData(AEDataAttachments.CLIMBABLE)
                .map(data -> data.climbPosition != null)
                .orElse(false);
    }
}
