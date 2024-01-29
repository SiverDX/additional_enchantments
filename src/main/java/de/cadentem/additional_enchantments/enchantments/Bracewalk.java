package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Bracewalk extends ConfigurableEnchantment {
    public Bracewalk() {
        super(Rarity.COMMON, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS, AEEnchantments.BRACEWALK_ID);
    }

    @SubscribeEvent
    public static void breakBlocks(final LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getLevel().isClientSide() || /* Players can check more often */ !(entity instanceof Player) && entity.tickCount % 10 != 0) {
            return;
        }

        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.BRACEWALK.get(), entity);

        if (enchantmentLevel > 0) {
            BlockPos.betweenClosedStream(entity.getBoundingBox()).forEach(blockPosition -> {
                BlockState blockState = entity.getLevel().getBlockState(blockPosition);

                if (blockState.is(AEBlockTags.BRACEWALK)) {
                    entity.getLevel().destroyBlock(blockPosition, true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void reduceKnockBack(final LivingKnockBackEvent event) {
        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(AEEnchantments.BRACEWALK.get(), event.getEntity());

        if (enchantmentLevel > 0) {
            event.setStrength(event.getStrength() * Math.max(0, 1 - (enchantmentLevel / 7f)));
        }
    }
}
