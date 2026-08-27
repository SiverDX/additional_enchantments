package de.cadentem.additional_enchantments.enchantments.treasure_finder;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.TreasureFinderData;
import de.cadentem.additional_enchantments.common.network.NetworkHandler;
import de.cadentem.additional_enchantments.common.network.SyncTreasureFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TreasureFinderEffect(LevelBasedTreasureFinder vision) implements EnchantmentEntityEffect {
    public static final MapCodec<TreasureFinderEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedTreasureFinder.CODEC.fieldOf("vision").forGetter(TreasureFinderEffect::vision)
    ).apply(instance, TreasureFinderEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<TreasureFinder.Mapped> visions = vision.get(enchantmentLevel);

            TreasureFinderData data = player.getData(AEDataAttachments.TREASURE_FINDER);
            data.addVisions(visions);

            PacketDistributor.sendToPlayer(player, new SyncTreasureFinder(visions, NetworkHandler.SyncType.ADD));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            List<TreasureFinder.Mapped> visions = vision.get(enchantmentLevel);

            TreasureFinderData data = player.getData(AEDataAttachments.TREASURE_FINDER);
            data.removeVisions(visions.stream().map(TreasureFinder.Mapped::id).toList());

            PacketDistributor.sendToPlayer(player, new SyncTreasureFinder(visions, NetworkHandler.SyncType.REMOVE));
        }
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedTreasureFinder.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedTreasureFinder.Entry... entries) {
        return new TreasureFinderEffect(new LevelBasedTreasureFinder(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
