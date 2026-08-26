package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.BlockVisionData;
import de.cadentem.additional_enchantments.common.network.SyncBlockVision;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BlockVisionEffect(LevelBasedBlockVision vision) implements EnchantmentEntityEffect {
    public static final MapCodec<BlockVisionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedBlockVision.CODEC.fieldOf("vision").forGetter(BlockVisionEffect::vision)
    ).apply(instance, BlockVisionEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<BlockVision.Mapped> visions = vision.get(enchantmentLevel);

            BlockVisionData data = player.getData(AEDataAttachments.BLOCK_VISION);
            data.addVisions(visions);

            PacketDistributor.sendToPlayer(player, new SyncBlockVision(visions, false));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            List<BlockVision.Mapped> visions = vision.get(enchantmentLevel);

            BlockVisionData data = player.getData(AEDataAttachments.BLOCK_VISION);
            data.removeVisions(visions.stream().map(BlockVision.Mapped::id).toList());

            PacketDistributor.sendToPlayer(player, new SyncBlockVision(visions, true));
        }
    }

    public static List<EnchantmentEntityEffect> single(final LevelBasedBlockVision.Entry... entries) {
        return List.of(create(entries));
    }

    public static EnchantmentEntityEffect create(final LevelBasedBlockVision.Entry... entries) {
        return new BlockVisionEffect(new LevelBasedBlockVision(List.of(entries)));
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
