package de.cadentem.additional_enchantments.capability;

import com.google.common.collect.Sets;
import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
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

    public void handleHomingMovement(final Projectile instance) {
        if (homingTarget == null) {
            if (homingTargetId != -1) {
                Entity entity = instance.getLevel().getEntity(homingTargetId);

                if (entity instanceof LivingEntity livingEntity) {
                    homingTarget = livingEntity;
                } else {
                    homingTargetId = -1;
                    return;
                }
            } else {
                return;
            }
        }

        if (homingTarget.isRemoved()) {
            homingTarget = null;
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
        if (homingTarget != null || !(instance.getOwner() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ConfigurationProvider.getCapability(serverPlayer).ifPresent(configuration -> {
            if (configuration.homingTypeFilter == HomingEnchantment.TypeFilter.NONE) {
                return;
            }

            List<LivingEntity> entities = serverPlayer.getLevel().getEntitiesOfClass(LivingEntity.class, instance.getBoundingBox().inflate(5 + homingEnchantmentLevel * 2), entity -> {
                if (entity.getType().is(EntityTags.HOMING_BLACKLIST)) {
                    return false;
                }

                if (entity.isInvulnerable() || (entity.isInvisible() && !entity.isCurrentlyGlowing())) {
                    return false;
                }

                if (configuration.homingTypeFilter == HomingEnchantment.TypeFilter.MONSTER && !(entity instanceof Monster)) {
                    return false;
                }

                if (configuration.homingTypeFilter == HomingEnchantment.TypeFilter.ANIMAL && (!(entity instanceof Animal))) {
                    return false;
                }

                if (configuration.homingTypeFilter == HomingEnchantment.TypeFilter.BOSSES && !(entity.getType().is(Tags.EntityTypes.BOSSES))) {
                    return false;
                }

                // Don't target owner or allies
                return serverPlayer != entity && !entity.isAlliedTo(serverPlayer) && (!(entity instanceof TamableAnimal tamable) || !tamable.isOwnedBy(serverPlayer));
            });

            if (entities.isEmpty()) {
                return;
            }

            LivingEntity target;

            if (configuration.homingPriority == HomingEnchantment.Priority.RANDOM) {
                target = entities.get(serverPlayer.getRandom().nextInt(entities.size()));
            } else {
                entities.sort((a, b) -> {
                    if (configuration.homingPriority == HomingEnchantment.Priority.CLOSEST) {
                        return Float.compare(a.distanceTo(instance), b.distanceTo(instance));
                    } else if (configuration.homingPriority == HomingEnchantment.Priority.LOWEST_HEALTH) {
                        return Float.compare((a.getMaxHealth() / 100) * a.getHealth(), b.getMaxHealth() / 100 * b.getHealth());
                    } else if (configuration.homingPriority == HomingEnchantment.Priority.HIGHEST_HEALTH) {
                        return Float.compare((a.getMaxHealth() / 100) * a.getHealth(), b.getMaxHealth() / 100 * b.getHealth()) * -1;
                    }

                    return 0;
                });

                target = entities.get(0);
            }

            homingTarget = target;
            homingTargetId = target.getId();
            CapabilityHandler.syncProjectileData(instance);
        });
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tippedEnchantmentLevel", tippedEnchantmentLevel);
        tag.putInt("homingEnchantmentLevel", homingEnchantmentLevel);
        tag.putInt("explosiveTipEnchantmentLevel", explosiveTipEnchantmentLevel);
        tag.putInt("straightShotEnchantmentLevel", straightShotEnchantmentLevel);
        tag.putInt("homingTargetId", homingTargetId);

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

        ListTag effects = tag.getList("addedEffects", ListTag.TAG_COMPOUND);

        if (!hasAddedEffects()) {
            addedEffects = Sets.newHashSet();
        }

        for (int i = 0; i < effects.size(); i++) {
            addedEffects.add(MobEffectInstance.load(effects.getCompound(i)));
        }
    }

    public boolean hasAddedEffects() {
        return addedEffects != null && !addedEffects.isEmpty();
    }
}
