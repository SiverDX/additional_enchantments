package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.enchantments.HunterEnchantment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class HunterLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public HunterLayer(final RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull final PoseStack poseStack, @NotNull final MultiBufferSource bufferSource, int packedLight, @NotNull final AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.isInvisible() || player.isSpectator()) {
            return;
        }

        int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel(player);

        if (enchantmentLevel > 0 && !player.hasEffect(MobEffects.INVISIBILITY)) {
            PlayerDataProvider.getCapability(player).ifPresent(data -> {
                if (data.hasHunterStacks()) {
                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(player.getSkinTextureLocation()));
                    getParentModel().renderToBuffer(poseStack, buffer, packedLight, PlayerRenderer.getOverlayCoords(player, 0), 1, 1, 1, getAlpha(data.getHunterStacks(), enchantmentLevel));
                }
            });
        }
    }

    @SubscribeEvent
    public static void updateHunterStacks(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        PlayerDataProvider.getCapability(event.player).ifPresent(data -> {
            int enchantmentLevel = HunterEnchantment.getClientEnchantmentLevel(event.player);
            data.reduceDelayStacks();

            if (enchantmentLevel > 0) {
                if (/* Basically inside the block */ HunterEnchantment.isBlockHunterRelevant(event.player.getFeetBlockState()) || /* Below feet */ HunterEnchantment.isBlockHunterRelevant(event.player.getBlockStateOn())) {
                    data.increaseHunterStacks(event.player, enchantmentLevel);
                } else {
                    data.reduceHunterStacks(event.player, enchantmentLevel);
                }
            } else {
                data.clearHunterStacks(event.player);
            }
        });
    }

    public static float getAlpha(int hunterStacks, int enchantmentLevel) {
        return 1f - (float) hunterStacks / HunterEnchantment.getMaxStacks(enchantmentLevel);
    }
}
