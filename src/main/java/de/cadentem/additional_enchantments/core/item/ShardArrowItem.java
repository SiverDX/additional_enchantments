package de.cadentem.additional_enchantments.core.item;

import de.cadentem.additional_enchantments.core.entity.ShardArrow;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShardArrowItem extends ArrowItem {
    public ShardArrowItem(final Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull final Level level, @NotNull final ItemStack stack, @NotNull final LivingEntity shooter) {
        ShardArrow shardArrow = new ShardArrow(level, shooter);
        shardArrow.enchantmentLevel = shooter.getMainHandItem().getEnchantmentLevel(AEEnchantments.SHATTER.get());
        return shardArrow;
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack, @Nullable final Level level, @NotNull final List<Component> components, @NotNull final TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, components, isAdvanced);
        components.add(Component.translatable("item.additional_enchantments.shard_arrow.description").withStyle(ChatFormatting.DARK_GRAY));
    }
}
