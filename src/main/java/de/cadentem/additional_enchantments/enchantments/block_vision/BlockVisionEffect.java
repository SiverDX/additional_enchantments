package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.BlockVisionData;
import de.cadentem.additional_enchantments.common.network.SyncBlockVision;
import de.cadentem.additional_enchantments.enchantments.AEEnchantmentRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public record BlockVisionEffect(LevelBasedBlockVision vision) implements EnchantmentLocationBasedEffect {
    public static final MapCodec<BlockVisionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedBlockVision.CODEC.fieldOf("vision").forGetter(BlockVisionEffect::vision)
    ).apply(instance, BlockVisionEffect::new));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyEffect(final LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        List<BlockVision> visions = null;
        BlockVisionData vision = player.getData(AEDataAttachments.BLOCK_VISION);

        if (EnchantmentHelper.has(event.getFrom(), AEEnchantmentRegistry.BLOCK_VISION_COMPONENT.value())) {
            vision.setVision(null);
            visions = List.of();
        }

        if (EnchantmentHelper.has(event.getTo(), AEEnchantmentRegistry.BLOCK_VISION_COMPONENT.value())) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : event.getTo().getTagEnchantments().entrySet()) {
                BlockVisionEffect blockVision = entry.getKey().value().effects().get(AEEnchantmentRegistry.BLOCK_VISION_COMPONENT.value());
                
                if (blockVision != null) {
                    visions = blockVision.vision().get(entry.getIntValue());
                    vision.setVision(visions);
                    break;
                }
            }
        }

        if (visions != null) {
            PacketDistributor.sendToPlayer(player, new SyncBlockVision(visions));
        }
    }

    @Override
    public void onChangedBlock(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 position, boolean hasEffect) {
        // Handled by event due to custom component type
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentLocationBasedEffect> codec() {
        return CODEC;
    }
}
