package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.capability.ConfigurationProvider;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class HunterLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public HunterLayer(final RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    // Avoids invisibility flicker (due to server still having a `hunterState` above 0)
    private static final int MAX_DELAY = 10;
    private int delay = MAX_DELAY;

    @Override
    public void render(@NotNull final PoseStack poseStack, @NotNull final MultiBufferSource bufferSource, int packedLight, @NotNull final AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.isSpectator()) {
            return;
        }

        ConfigurationProvider.getCapability(player).ifPresent(configuration -> {
            if (configuration.hunterStacks > 0 || delay > 0) {
                int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel();

                if (enchantmentLevel == 0) {
                    delay = 0;
                    return;
                }

                if (configuration.hasMaxHunterStacks(enchantmentLevel)) {
                    delay--;

                    if (delay <= 0) {
                        return;
                    }
                } else {
                    delay = MAX_DELAY;
                }

                float alpha = 1f - (float) configuration.hunterStacks / HunterEnchantment.getMaxStacks(enchantmentLevel);

                VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(player.getSkinTextureLocation()));
                getParentModel().renderToBuffer(poseStack, buffer, packedLight, PlayerRenderer.getOverlayCoords(player, 0), 1, 1, 1, alpha);
            }
        });
    }

    @SubscribeEvent
    public static void updateHunterStacks(final LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity != ClientProxy.getLocalPlayer()) {
            return;
        }

        int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel();

        if (enchantmentLevel > 0) {
            if (/* Basically inside the block */ HunterEnchantment.isBlockHunterRelevant(livingEntity.getFeetBlockState()) || /* Below feet */ HunterEnchantment.isBlockHunterRelevant(livingEntity.getBlockStateOn())) {
                ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> configuration.increaseHunterStacks(enchantmentLevel));
            } else {
                ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> configuration.reduceHunterStacks(livingEntity, enchantmentLevel));
            }
        } else {
            ConfigurationProvider.getCapability(livingEntity).ifPresent(configuration -> configuration.hunterStacks = 0);
        }
    }
}
