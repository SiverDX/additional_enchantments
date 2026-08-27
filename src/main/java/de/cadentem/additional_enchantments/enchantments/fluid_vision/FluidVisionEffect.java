package de.cadentem.additional_enchantments.enchantments.fluid_vision;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.FluidVisionData;
import de.cadentem.additional_enchantments.common.network.NetworkHandler;
import de.cadentem.additional_enchantments.common.network.SyncFluidVision;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FluidVisionEffect(LevelBasedFluidVision vision) implements EnchantmentEntityEffect {
    public static final MapCodec<FluidVisionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedFluidVision.CODEC.fieldOf("vision").forGetter(FluidVisionEffect::vision)
    ).apply(instance, FluidVisionEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<FluidVision.Mapped> visions = vision.get(enchantmentLevel);

            FluidVisionData data = player.getData(AEDataAttachments.FLUID_VISION);
            data.addVisions(visions);

            PacketDistributor.sendToPlayer(player, new SyncFluidVision(visions, NetworkHandler.SyncType.ADD));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            List<FluidVision.Mapped> visions = vision.get(enchantmentLevel);

            FluidVisionData data = player.getData(AEDataAttachments.FLUID_VISION);
            data.removeVisions(visions.stream().map(FluidVision.Mapped::id).toList());

            PacketDistributor.sendToPlayer(player, new SyncFluidVision(visions, NetworkHandler.SyncType.REMOVE));
        }
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedFluidVision.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedFluidVision.Entry... entries) {
        return new FluidVisionEffect(new LevelBasedFluidVision(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
