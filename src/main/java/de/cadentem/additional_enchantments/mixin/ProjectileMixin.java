package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import de.cadentem.additional_enchantments.core.interfaces.LivingEntityAccess;
import de.cadentem.additional_enchantments.core.interfaces.ProjectileAccess;
import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import de.cadentem.additional_enchantments.network.NetworkHandler;
import de.cadentem.additional_enchantments.network.SyncHomingData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements ProjectileAccess {
    // TODO :: re-set context on change owner call?
    @Unique
    public HomingEnchantment.HomingContext additional_enchantments$homingContext;

    @Unique
    public int additional_enchantments$explosiveTipEnchantmentLevel;

    @Unique
    public int additional_enchantments$straightShotEnchantmentLevel;

    public ProjectileMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    public HomingEnchantment.HomingContext additional_enchantments$getHomingContext() {
        return additional_enchantments$homingContext;
    }

    @Override
    public void additional_enchantments$setHomingContext(final HomingEnchantment.HomingContext homingContext) {
        additional_enchantments$homingContext = homingContext;
    }

    @Override
    public int additional_enchantments$getExplosiveTipEnchantmentLevel() {
        return additional_enchantments$explosiveTipEnchantmentLevel;
    }

    @Override
    public void additional_enchantments$setExplosiveTipEnchantmentLevel(int explosiveTipEnchantmentLevel) {
        this.additional_enchantments$explosiveTipEnchantmentLevel = explosiveTipEnchantmentLevel;
    }

    @Override
    public int additional_enchantments$getStraightShotEnchantmentLevel() {
        return additional_enchantments$straightShotEnchantmentLevel;
    }

    @Override
    public void additional_enchantments$setStraightShotEnchantmentLevel(int straightShotEnchantmentLevel) {
        this.additional_enchantments$straightShotEnchantmentLevel = straightShotEnchantmentLevel;
    }

    /**
     * Target gets selected server-side<br>
     * Movement gets handled on both sides
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void additional_enchantments$searchForTarget(final CallbackInfo callback) {
        if (additional_enchantments$homingContext == null) {
            return;
        }

        Projectile instance = (Projectile) (Object) this;

        if (additional_enchantments$homingContext.target != null) {
            if (additional_enchantments$homingContext.target.isRemoved()) {
                ((LivingEntityAccess) additional_enchantments$homingContext.target).additional_enchantments$removeTracked();
                additional_enchantments$homingContext.target = null;
                return;
            }

            Vec3 velocity = instance.getDeltaMovement();
            Vec3 motion = instance.position().vectorTo(additional_enchantments$homingContext.target.position().add(0, additional_enchantments$homingContext.target.getEyeHeight() / 2, 0));

            motion = motion.normalize();
            // Adjust it to the original velocity
            motion = motion.scale(velocity.length() * (0.95 + additional_enchantments$homingContext.enchantmentLevel * 0.01));

            instance.setDeltaMovement(motion);

            return;
        }

        if (!(instance.getOwner() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        CapabilityProvider.getCapability(serverPlayer).ifPresent(configuration -> {
            if (configuration.homingTypeFilter == HomingEnchantment.TypeFilter.NONE) {
                return;
            }

            List<LivingEntity> entities = serverPlayer.level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5 + additional_enchantments$homingContext.enchantmentLevel * 2), entity -> {
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

            /* TODO -> sort by lowest amount of tracked by?
                ((TrackedInterface) entity).additional_enchantments$getTrackedByCount()
            */

            ((LivingEntityAccess) target).additional_enchantments$addTracked();
            additional_enchantments$homingContext.target = target;

            // Sync target to client for accurate movement of the projectile
            serverPlayer.level().players().stream().filter(player -> player.distanceToSqr(instance) <= 32 * 32).forEach(player -> NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncHomingData(instance.getId(), target.getId(), additional_enchantments$homingContext.enchantmentLevel)));
        });
    }
}
