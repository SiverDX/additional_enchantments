package de.cadentem.additional_enchantments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.common.network.SyncClimbFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Unique private static final EntityDimensions CEILING_CLIMBING_DIMENSIONS = EntityDimensions.fixed(0.6f, 0.2f).withEyeHeight(0.2f);

    @ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
    private boolean additional_enchantments$canStickToWalls(final boolean original) {
        ClimbableData data = getExistingData(AEDataAttachments.CLIMBABLE_DATA).orElse(null);

        if (data == null || data.climbPosition == null) {
            return original;
        }

        if (/* We use this to allow players to move down */ isShiftKeyDown()) {
            return false;
        }

        if (level() instanceof WorldGenLevel level) {
            return data.canStickToWalls(level);
        }

        return data.isApprovedClimbPosition(data.climbPosition);
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions getDimensions(final EntityDimensions original) {
        ClimbableData data = getData(AEDataAttachments.CLIMBABLE_DATA);

        if (data.climbingType == SyncClimbFlag.ClimbingType.CEILING) {
            return CEILING_CLIMBING_DIMENSIONS;
        }

        return original;
    }
}
