package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.cadentem.additional_enchantments.util.Proxy;
import net.minecraft.world.entity.LivingEntity;

public class ClientProxy implements Proxy {
    @Override
    public float getTimer() {
        return AEClient.TIMER;
    }

    public static void test(final LivingEntity entity, final PoseStack pose) {
        pose.translate(0, entity.getBbHeight(), 0);
        pose.mulPose(Axis.XP.rotationDegrees(90));
        // Need to invert the facing direction for movement since the model is inverted
        pose.mulPose(Axis.ZP.rotationDegrees(-180));
    }
}
