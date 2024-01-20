package de.cadentem.additional_enchantments.core.item;

import de.cadentem.additional_enchantments.core.entity.ShardArrow;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ShardArrowItem extends ArrowItem {
    public ShardArrowItem(final Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull final Level level, @NotNull final ItemStack stack, @NotNull final LivingEntity shooter) {
        ShardArrow shardArrow = new ShardArrow(AEEntityTypes.SHARD_ARROW.get(), level);
        shardArrow.enchantmentLevel = shooter.getMainHandItem().getEnchantmentLevel(AEEnchantments.SHATTER.get());
        shardArrow.setEffectsFromItem(stack);
        shardArrow.setOwner(shooter);
        shardArrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        return shardArrow;
    }
}
