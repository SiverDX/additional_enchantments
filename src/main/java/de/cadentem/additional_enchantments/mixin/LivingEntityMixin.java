package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.enchantments.climbing.CeilingClimbDimensions;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbingHandler;
import de.cadentem.additional_enchantments.util.IBoundingBoxOffset;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
    private boolean additional_enchantments$canStickToWalls(final boolean original) {
        ClimbableData data = getExistingData(AEDataAttachments.CLIMBABLE_DATA).orElse(null);

        if (data == null || data.climbPosition == null) {
            return original;
        }

        if (ClimbingHandler.canDescend((LivingEntity) (Object) this, data)) {
            // SHIFT is usually used to stay in place, but in this case it's the opposite
            // Also prevent going down if it could cause clipping (after the hitbox is reverted to its original height)
            return false;
        }

        if (level() instanceof WorldGenLevel level) {
            return data.canStickToWalls(level);
        }

        return data.isApprovedClimbPosition(data.climbPosition);
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions additional_enchantments$ceilingClimbingDimensions(final EntityDimensions original, @Local(argsOnly = true) final Pose pose) {
        boolean isCeilingClimbing = getData(AEDataAttachments.CLIMBABLE_DATA).isCeilingClimbing();

        if (pose == getPose()) {
            // Only keep track of the offset for the current active pose
            ((IBoundingBoxOffset) this).additional_enchantments$setBoundingBoxOffset(isCeilingClimbing ? CeilingClimbDimensions.adjustOffset(original) : 0);
        }

        if (isCeilingClimbing) {
            return CeilingClimbDimensions.adjust(original);
        }

        return original;
    }
}
