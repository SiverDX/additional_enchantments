package de.cadentem.additional_enchantments.capability;

import com.google.common.collect.Sets;
import de.cadentem.additional_enchantments.data.AEEntityTags;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import de.cadentem.additional_enchantments.mixin.AbstractArrowAccess;
import de.cadentem.additional_enchantments.mixin.TridentAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Set;

public class ProjectileData {
    public Set<MobEffectInstance> addedEffects;
    public int tippedEnchantmentLevel;

    public LivingEntity homingTarget;
    public int homingTargetId;
    public int homingEnchantmentLevel;

    public int explosiveTipEnchantmentLevel;

    public int straightShotEnchantmentLevel;
    public int gravityTime;

    public void handleHomingMovement(final Projectile instance) {
        if (homingTarget == null) {
            if (homingTargetId != -1) {
                Entity entity = instance.getLevel().getEntity(homingTargetId);

                if (entity instanceof LivingEntity livingEntity) {
                    homingTarget = livingEntity;
                } else {
                    homingTargetId = -1;
                }
            }
        }

        if (isInvalidTarget(homingTarget) || isInvalidProjectile(instance)) {
            setHomingTarget(instance, null);
            return;
        }

        Vec3 velocity = instance.getDeltaMovement();
        Vec3 motion = instance.position().vectorTo(homingTarget.position().add(0, homingTarget.getEyeHeight() / 2, 0));

        motion = motion.normalize();
        // Adjust it to the original velocity
        motion = motion.scale(velocity.length() * (0.95 + homingEnchantmentLevel * 0.01));

        instance.setDeltaMovement(motion);
    }

    public void searchForHomingTarget(final Projectile instance) {
        if (homingTarget != null || !(instance.getOwner() instanceof ServerPlayer serverPlayer) || isInvalidProjectile(instance)) {
            return;
        }

        PlayerDataProvider.getCapability(serverPlayer).ifPresent(playerData -> {
            if (playerData.homingTypeFilter == HomingEnchantment.TypeFilter.NONE) {
                return;
            }

            List<LivingEntity> entities = serverPlayer.getLevel().getEntitiesOfClass(LivingEntity.class, instance.getBoundingBox().inflate(5 + homingEnchantmentLevel * 2), entity -> {
                if (isInvalidTarget(entity)) {
                    return false;
                }

                if (entity.getType().is(AEEntityTags.HOMING_BLACKLIST)) {
                    return false;
                }

                if (entity.isInvulnerable() || (entity.isInvisible() && !entity.isCurrentlyGlowing())) {
                    return false;
                }

                if (playerData.homingTypeFilter == HomingEnchantment.TypeFilter.MONSTER && !(entity instanceof Monster)) {
                    return false;
                }

                if (playerData.homingTypeFilter == HomingEnchantment.TypeFilter.ANIMAL && (!(entity instanceof Animal))) {
                    return false;
                }

                if (playerData.homingTypeFilter == HomingEnchantment.TypeFilter.BOSSES && !(entity.getType().is(Tags.EntityTypes.BOSSES))) {
                    return false;
                }

                // Don't target owner or allies
                return serverPlayer != entity && !entity.isAlliedTo(serverPlayer) && (!(entity instanceof TamableAnimal tamable) || !tamable.isOwnedBy(serverPlayer));
            });

            if (entities.isEmpty()) {
                return;
            }

            LivingEntity target;

            if (playerData.homingPriority == HomingEnchantment.Priority.RANDOM) {
                target = entities.get(serverPlayer.getRandom().nextInt(entities.size()));
            } else {
                entities.sort((a, b) -> {
                    if (playerData.homingPriority == HomingEnchantment.Priority.CLOSEST) {
                        return Float.compare(a.distanceTo(instance), b.distanceTo(instance));
                    } else if (playerData.homingPriority == HomingEnchantment.Priority.LOWEST_HEALTH) {
                        return Float.compare((a.getMaxHealth() / 100) * a.getHealth(), b.getMaxHealth() / 100 * b.getHealth());
                    } else if (playerData.homingPriority == HomingEnchantment.Priority.HIGHEST_HEALTH) {
                        return Float.compare((a.getMaxHealth() / 100) * a.getHealth(), b.getMaxHealth() / 100 * b.getHealth()) * -1;
                    }

                    return 0;
                });

                target = entities.get(0);
            }

            setHomingTarget(instance, target);
        });
    }

    public void handleImpact(final Projectile projectile) {
        if (straightShotEnchantmentLevel > 0) {
            projectile.setNoGravity(false);
        }

        setHomingTarget(projectile, null);
    }

    public boolean hasAddedEffects() {
        return addedEffects != null && !addedEffects.isEmpty();
    }

    private void setHomingTarget(final Projectile instance, final LivingEntity target) {
        if (target == null) {
            homingTarget = null;
            homingTargetId = -1;
        } else {
            homingTarget = target;
            homingTargetId = target.getId();
        }

        CapabilityHandler.syncProjectileData(instance);
    }

    private boolean isInvalidTarget(final LivingEntity target) {
        if (target == null) {
            return true;
        }

        return target.isRemoved() || target.isDeadOrDying();
    }

    private boolean isInvalidProjectile(final Projectile projectile) {
        if (projectile instanceof AbstractArrowAccess arrow && arrow.isInGround()) {
            return true;
        }

        if (projectile instanceof TridentAccess trident && trident.didDealDamage()) {
            return true;
        }

        return projectile.isRemoved();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tippedEnchantmentLevel", tippedEnchantmentLevel);
        tag.putInt("homingEnchantmentLevel", homingEnchantmentLevel);
        tag.putInt("explosiveTipEnchantmentLevel", explosiveTipEnchantmentLevel);
        tag.putInt("straightShotEnchantmentLevel", straightShotEnchantmentLevel);
        tag.putInt("homingTargetId", homingTargetId);
        tag.putInt("gravityTime", gravityTime);

        if (hasAddedEffects()) {
            ListTag effects = new ListTag();

            for (MobEffectInstance effect : addedEffects) {
                effects.add(effect.save(new CompoundTag()));
            }

            tag.put("addedEffects", effects);
        }

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        tippedEnchantmentLevel = tag.getInt("tippedEnchantmentLevel");
        homingEnchantmentLevel = tag.getInt("homingEnchantmentLevel");
        explosiveTipEnchantmentLevel = tag.getInt("explosiveTipEnchantmentLevel");
        straightShotEnchantmentLevel = tag.getInt("straightShotEnchantmentLevel");
        homingTargetId = tag.getInt("homingTargetId");
        gravityTime = tag.getInt("gravityTime");

        ListTag effects = tag.getList("addedEffects", ListTag.TAG_COMPOUND);

        if (!hasAddedEffects()) {
            addedEffects = Sets.newHashSet();
        }

        for (int i = 0; i < effects.size(); i++) {
            addedEffects.add(MobEffectInstance.load(effects.getCompound(i)));
        }
    }
}
