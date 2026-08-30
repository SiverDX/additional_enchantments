package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbingHandler;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBlockExtension.class)
public interface IBlocksExtensionMixin {
    @ModifyReturnValue(method = "isLadder", at = @At("RETURN"))
    default boolean additional_enchantments$isLadder(boolean original, @Local(argsOnly = true, name = "entity") final LivingEntity entity) {
        if (entity == null) {
            // MineColonies may cause this to be null
            return original;
        }

        ClimbableData data = entity.getExistingData(AEDataAttachments.CLIMBABLE).orElse(null);

        if (data == null || original) {
            return original;
        }

        return ClimbingHandler.canClimb(entity, data);
    }
}
