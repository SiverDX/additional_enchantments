package de.cadentem.additional_enchantments.core.entity;

import de.cadentem.additional_enchantments.registry.AEEntityTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class ShardArrow extends AbstractArrow {
    private static final ParticleOptions PARTICLES = new ItemParticleOption(ParticleTypes.ITEM, Items.AMETHYST_SHARD.getDefaultInstance());

    public int enchantmentLevel;

    public ShardArrow(final EntityType<ShardArrow> type, final Level level) {
        super(type, level);
    }

    public ShardArrow(final Level level, final LivingEntity shooter) {
        super(AEEntityTypes.SHARD_ARROW.get(), shooter, level);
    }

    @Override
    protected void onHitEntity(@NotNull final EntityHitResult result) {
        super.onHitEntity(result);
        handleHit();
    }

    @Override
    protected void onHitBlock(@NotNull final BlockHitResult result) {
        super.onHitBlock(result);
        handleHit();
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return Items.AMETHYST_SHARD.getDefaultInstance();
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.LARGE_AMETHYST_BUD_BREAK;
    }

    @Override
    public void setSoundEvent(@NotNull final SoundEvent soundEvent) {
        if (soundEvent == SoundEvents.CROSSBOW_HIT) {
            return;
        }

        super.setSoundEvent(soundEvent);
    }

    private void handleHit() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.getRandom().nextDouble() > 0.3) {
                sendParticles();

                // Cannot handle the damage while gathering entities due to possible `ConcurrentModificationException`
                serverLevel.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(2 + enchantmentLevel), livingEntity -> {
                    Entity owner = getOwner();

                    if (owner == null) {
                        return true;
                    }

                    if (livingEntity == owner || livingEntity.isAlliedTo(owner)) {
                        return false;
                    }

                    return !(owner instanceof LivingEntity livingOwner) || !(livingEntity instanceof TamableAnimal tamable) || !tamable.isOwnedBy(livingOwner);
                }).forEach(livingEntity -> livingEntity.hurt(DamageSource.arrow(this, getOwner()).setMagic(), enchantmentLevel / 2f));

                discard();
            }
        }
    }

    private void sendParticles() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            double xzOffset = getBbWidth() * enchantmentLevel;
            double yOffset = getBbHeight() * enchantmentLevel;
            int particleCount = (int) (8 * Math.pow(enchantmentLevel, 1.5));

            serverLevel.sendParticles(PARTICLES, getX(), getY(), getZ(), Math.min(1000, particleCount), xzOffset, yOffset, xzOffset, 0);
        }
    }
}
