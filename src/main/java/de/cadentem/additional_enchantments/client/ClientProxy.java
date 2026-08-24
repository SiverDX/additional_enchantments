package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.cadentem.additional_enchantments.enchantments.climbing.CeilingClimbDimensions;
import de.cadentem.additional_enchantments.util.Proxy;
import net.minecraft.world.entity.LivingEntity;

public class ClientProxy implements Proxy {
    @Override
    public float getTimer() {
        return AEClient.TIMER;
    }

    public static void test(final LivingEntity entity, final PoseStack pose) {
        // The hitbox is usually re-sized to the bottom part - but to keep to the ceiling we need to inverse that behaviour
        // Which also means we need to move the model up to be at the hitbox again
        double unmodifiedHeight = CeilingClimbDimensions.getUnmodifiedHeight(entity);

        // Due to the rotation, the model has basically "fallen over" so we move it back to keep the head at the hitbox
        pose.translate(0, unmodifiedHeight, unmodifiedHeight);
        pose.mulPose(Axis.XP.rotationDegrees(90));
        // Need to invert the facing direction for movement since the model is inverted
        pose.mulPose(Axis.ZP.rotationDegrees(-180));
    }
}
