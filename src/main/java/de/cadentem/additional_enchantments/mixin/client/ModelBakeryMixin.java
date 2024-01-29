package de.cadentem.additional_enchantments.mixin.client;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Unique
    private static final ResourceLocation additional_enchantments$CROSSBOW = new ResourceLocation("item/crossbow");

    @Inject(method = "loadBlockModel", at = @At("RETURN"))
    private void additional_enchantments$addOverride(final ResourceLocation location, final CallbackInfoReturnable<BlockModel> callback) {
        if (location.equals(additional_enchantments$CROSSBOW)) {
            callback.getReturnValue().getOverrides().add(new ItemOverride(new ResourceLocation(AE.MODID, "item/crossbow_shard"), List.of(
                new ItemOverride.Predicate(new ResourceLocation("charged"), 1),
                new ItemOverride.Predicate(new ResourceLocation(AE.MODID, "shard"), 1)
            )));
        }
    }
}
