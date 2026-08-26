package de.cadentem.additional_enchantments.enchantments.climbing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.ClimbableData;
import de.cadentem.additional_enchantments.common.network.SyncClimbable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClimbableEffect(LevelBasedClimbable climbable) implements EnchantmentEntityEffect {
    public static final MapCodec<ClimbableEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedClimbable.CODEC.fieldOf("climbable").forGetter(ClimbableEffect::climbable)
    ).apply(instance, ClimbableEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<Climbable> climbables = climbable.get(enchantmentLevel);

            ClimbableData data = player.getData(AEDataAttachments.CLIMBABLE);
            data.addClimbables(climbables);

            PacketDistributor.sendToPlayer(player, new SyncClimbable(climbables, false));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            List<Climbable> climbables = climbable.get(enchantmentLevel);

            ClimbableData data = player.getData(AEDataAttachments.CLIMBABLE);
            data.removeClimbables(climbables.stream().map(Climbable::id).toList());

            PacketDistributor.sendToPlayer(player, new SyncClimbable(climbables, true));
        }
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedClimbable.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedClimbable.Entry... entries) {
        return new ClimbableEffect(new LevelBasedClimbable(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
