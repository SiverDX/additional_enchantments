package de.cadentem.additional_enchantments.client;

import com.google.common.reflect.TypeToken;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.common.network.SyncClimbFlag;
import de.cadentem.additional_enchantments.enchantments.climbing.CeilingClimbDimensions;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class AERenderData {
    public static final ContextKey<CeilingClimbingRenderData> CLIMBING = new ContextKey<>(AE.location("climbing_type"));

    @SubscribeEvent
    public static void registerRenderStateModifiers(final RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>(){},
                (entity, state) -> {
                    ClimbableData data = entity.getExistingDataOrNull(AEDataAttachments.CLIMBABLE);

                    if (data == null) {
                        state.setRenderData(CLIMBING, CeilingClimbingRenderData.NONE);
                    } else {
                        state.setRenderData(CLIMBING, new CeilingClimbingRenderData(data.getClimbingType(), CeilingClimbDimensions.getUnmodifiedHeight(entity)));
                    }
                }
        );
    }

    public record CeilingClimbingRenderData(SyncClimbFlag.ClimbingType climbingType, double unmodifiedHeight) {
        public static CeilingClimbingRenderData NONE = new CeilingClimbingRenderData(SyncClimbFlag.ClimbingType.NONE, 0);
    }
}
