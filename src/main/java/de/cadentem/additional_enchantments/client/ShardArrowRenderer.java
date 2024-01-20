package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.entity.ShardArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ShardArrowRenderer extends ArrowRenderer<ShardArrow> {
    public static final ResourceLocation LOCATION = new ResourceLocation(AE.MODID, "textures/entity/projectiles/shard_arrow.png");

    public ShardArrowRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull final ShardArrow ignored) {
        return LOCATION;
    }
}
