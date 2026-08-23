package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.BlockVisionData;
import de.cadentem.additional_enchantments.common.network.SyncBlockVision;
import de.cadentem.additional_enchantments.enchantments.AEEnchantmentRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public record BlockVisionEffect(LevelBasedBlockVision vision) implements EnchantmentEntityEffect {
    public static final MapCodec<BlockVisionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedBlockVision.CODEC.fieldOf("vision").forGetter(BlockVisionEffect::vision)
    ).apply(instance, BlockVisionEffect::new));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyEffect(final LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        EnchantedItemInUse oldItem = new EnchantedItemInUse(event.getFrom(), event.getSlot(), event.getEntity());

        EnchantmentHelper.runIterationOnItem(event.getFrom(), (enchantment, enchantmentLevel) -> {
            enchantment.value().getEffects(AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value()).forEach(effect -> {
                effect.onDeactivated(oldItem, event.getEntity(), event.getEntity().position(), enchantmentLevel);
            });
        });

        EnchantedItemInUse newItem = new EnchantedItemInUse(event.getTo(), event.getSlot(), event.getEntity());

        EnchantmentHelper.runIterationOnItem(event.getTo(), (enchantment, enchantmentLevel) -> {
            enchantment.value().getEffects(AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value()).forEach(effect -> {
                effect.apply(player.serverLevel(), enchantmentLevel, newItem, event.getEntity(), event.getEntity().position());
            });
        });
    }

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position) {
        if (entity instanceof ServerPlayer player) {
            List<BlockVision> visions = vision.get(enchantmentLevel);

            BlockVisionData data = player.getData(AEDataAttachments.BLOCK_VISION);
            data.setVision(visions);

            PacketDistributor.sendToPlayer(player, new SyncBlockVision(visions));
        }
    }

    @Override
    public void onDeactivated(@NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, final int enchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(item, entity, position, enchantmentLevel);

        if (entity instanceof ServerPlayer player) {
            BlockVisionData data = player.getData(AEDataAttachments.BLOCK_VISION);
            data.setVision(null);

            PacketDistributor.sendToPlayer(player, new SyncBlockVision(List.of()));
        }
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
