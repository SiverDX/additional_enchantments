package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.HomingData;
import de.cadentem.additional_enchantments.attachments.PerceptionData;
import de.cadentem.additional_enchantments.common.network.NetworkHandler;
import de.cadentem.additional_enchantments.common.network.SyncHoming;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link net.minecraft.world.item.enchantment.EnchantmentEffectComponents#PROJECTILE_SPAWNED} only handles {@link net.minecraft.world.entity.projectile.AbstractArrow} </br>
 * Therefor handle it as usual with data attachments
 */
public record HomingEffect(LevelBasedHoming homing) implements EnchantmentEntityEffect {
    public static final MapCodec<HomingEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedHoming.CODEC.fieldOf("homing").forGetter(HomingEffect::homing)
    ).apply(instance, HomingEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        List<Homing.Mapped> entries = homing.get(enchantmentLevel);

        HomingData data = entity.getData(AEDataAttachments.HOMING);
        data.addHoming(entries);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SyncHoming(entity.getId(), entries, NetworkHandler.SyncType.ADD));
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        List<Homing.Mapped> entries = homing.get(enchantmentLevel);

        PerceptionData data = entity.getData(AEDataAttachments.PERCEPTION);
        data.removePerceptions(entries.stream().map(Homing.Mapped::id).toList());

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new SyncHoming(entity.getId(), entries, NetworkHandler.SyncType.REMOVE));
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedHoming.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedHoming.Entry... entries) {
        return new HomingEffect(new LevelBasedHoming(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
