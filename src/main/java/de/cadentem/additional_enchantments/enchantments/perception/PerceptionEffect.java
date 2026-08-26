package de.cadentem.additional_enchantments.enchantments.perception;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.PerceptionData;
import de.cadentem.additional_enchantments.common.network.SyncPerception;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PerceptionEffect(LevelBasedPerception perception) implements EnchantmentEntityEffect {
    public static final MapCodec<PerceptionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedPerception.CODEC.fieldOf("perception").forGetter(PerceptionEffect::perception)
    ).apply(instance, PerceptionEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<Perception.Mapped> perceptions = perception.get(enchantmentLevel);

            PerceptionData data = player.getData(AEDataAttachments.PERCEPTION);
            data.addPerceptions(perceptions);

            PacketDistributor.sendToPlayer(player, new SyncPerception(perceptions, false));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            List<Perception.Mapped> perceptions = perception.get(enchantmentLevel);

            PerceptionData data = player.getData(AEDataAttachments.PERCEPTION);
            data.removePerceptions(perceptions.stream().map(Perception.Mapped::id).toList());

            PacketDistributor.sendToPlayer(player, new SyncPerception(perceptions, true));
        }
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedPerception.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedPerception.Entry... entries) {
        return new PerceptionEffect(new LevelBasedPerception(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
