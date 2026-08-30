package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.common.network.SyncHomingProjectileData;
import de.cadentem.additional_enchantments.enchantments.homing.AimPoint;
import de.cadentem.additional_enchantments.enchantments.homing.Homing;
import de.cadentem.additional_enchantments.enchantments.homing.HomingRange;
import de.cadentem.additional_enchantments.mixin.AbstractArrowAccess;
import de.cadentem.additional_enchantments.mixin.TridentAccess;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ProjectileHomingData implements ValueIOSerializable {
    /** No limit to how much the projectile can turn for its target */
    public static final float NO_MAX_TURN = 180;

    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 EAST = new Vec3(1, 0, 0);

    /** The same value being used in {@link Vec3#normalize()} */
    private static final double EPSILON = 1.0E-4;

    private static final int NO_TARGET = -1;
    private static final int NO_VELOCITY_MULTIPLIER = 1;

    /** Ticks before the aim points are checked again */
    private static final int AIM_POINT_LIFETIME = 5;
    /** Ticks before a target is considered unreachable */
    private static final int MAX_BLOCKED_TICKS = 20;

    /** Only needed server-side to determine the target */
    private final Map<Identifier, Homing.Mapped> entries = new HashMap<>();
    /** Only needed server-side to determine the target */
    private HomingRange.Mapped maxSearchRange = HomingRange.Mapped.NONE;

    /** Transient data */
    private @Nullable Entity target;

    private int targetId = NO_TARGET;
    private float velocityMultiplier = NO_VELOCITY_MULTIPLIER;
    private float maxTurnPerTick = NO_MAX_TURN;

    private @Nullable AimPoint aimPoint;
    private int ticksUntilAimPointRefresh;
    private int blockedTicks;

    private boolean requiresSync;

    @SubscribeEvent
    public static void handleHoming(final EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }

        projectile.getExistingData(AEDataAttachments.PROJECTILE_HOMING_DATA).ifPresent(data -> {
            if (ProjectileHomingData.isInvalidProjectile(projectile)) {
                data.clearTarget();
                data.syncToClient(projectile);
                projectile.removeData(AEDataAttachments.PROJECTILE_HOMING_DATA);
                return;
            }

            data.handleTarget(projectile, data.entries.values());

            if (data.requiresSync && !projectile.level().isClientSide()) {
                data.syncToClient(projectile);
            }
        });
    }

    public void setEntries(final Map<Identifier, Homing.Mapped> entries) {
        this.entries.clear();
        this.entries.putAll(entries);

        maxSearchRange = HomingRange.Mapped.NONE;

        for (Homing.Mapped homing : this.entries.values()) {
            maxSearchRange = maxSearchRange.max(homing.searchRange());
        }
    }

    public void setClientData(final Entity target, int targetId, float velocityMultiplier, float maxTurnPerTick) {
        this.target = target;
        this.targetId = targetId;
        this.velocityMultiplier = velocityMultiplier;
        this.maxTurnPerTick = maxTurnPerTick;
    }

    public void syncToClient(final Projectile projectile) {
        if (projectile.level().isClientSide()) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(projectile, new SyncHomingProjectileData(projectile.getId(), targetId, velocityMultiplier, maxTurnPerTick));
        requiresSync = false;
    }

    private void handleTarget(final Projectile projectile, final Collection<Homing.Mapped> entries) {
        if (target == null && targetId != NO_TARGET) {
            target = projectile.level().getEntity(targetId);
        }

        if (isInvalidTarget(target)) {
            clearTarget();
            requiresSync = true;
        }

        if (targetId == NO_TARGET && projectile.level() instanceof ServerLevel serverLevel) {
            selectTarget(projectile, serverLevel, entries.stream().sorted(Comparator.comparingInt(Homing.Mapped::priority).reversed()).toList());
        }

        if (target == null) {
            return;
        }

        Vec3 aimPoint = selectAimPoint(projectile, target);

        if (aimPoint == null) {
            blockedTicks++;

            if (blockedTicks > MAX_BLOCKED_TICKS) {
                // Drop the target if it stays unreachable
                clearTarget();
                requiresSync = true;
            }

            return;
        }

        blockedTicks = 0;

        Vec3 velocity = projectile.getDeltaMovement();
        double speed = velocity.length();

        if (speed < EPSILON) {
            return;
        }

        Vec3 position = projectile.position();
        double ticksToImpact = Math.min(position.distanceTo(aimPoint) / speed, 5);

        // Attempt to find a better target position based on how fast the entity moves
        Vec3 predicted = aimPoint.add(target.getDeltaMovement().scale(ticksToImpact));
        Vec3 targetDirection = position.vectorTo(predicted).normalize();

        projectile.setDeltaMovement(rotate(velocity, targetDirection).scale(speed * velocityMultiplier));
    }

    /** Rotates the current direction towards the target direction */
    private Vec3 rotate(final Vec3 velocity, final Vec3 targetDirection) {
        if (maxTurnPerTick >= NO_MAX_TURN) {
            // Full turns allowed, so it doesn't matter
            return targetDirection;
        }

        Vec3 current = velocity.normalize();
        double maxTurnRadians = Math.toRadians(maxTurnPerTick);
        double requiredTurnRadians = Math.acos(Mth.clamp(current.dot(targetDirection), -1, 1));

        if (requiredTurnRadians <= maxTurnRadians) {
            return targetDirection;
        }

        // Determine which axis is needed to rotate towards the target
        Vec3 axis = current.cross(targetDirection);

        if (axis.lengthSqr() < EPSILON) {
            // Projectile moves in the opposite direction of the target, guess an axis
            axis = current.cross(Math.abs(current.y) < 0.99 ? UP : EAST);
        }

        axis = axis.normalize();

        // Rodrigues' rotation formula (simplified since the axis is perpendicular to the direction)
        return current.scale(Math.cos(maxTurnRadians)).add(axis.cross(current).scale(Math.sin(maxTurnRadians))).normalize();
    }

    private @Nullable Vec3 selectAimPoint(final Projectile projectile, final Entity target) {
        ticksUntilAimPointRefresh--;

        if (aimPoint != null && ticksUntilAimPointRefresh > 0) {
            return aimPoint.getPosition(target);
        }

        aimPoint = AimPoint.findFreePoint(projectile, target);

        if (aimPoint != null) {
            ticksUntilAimPointRefresh = AIM_POINT_LIFETIME;
            return aimPoint.getPosition(target);
        }

        ticksUntilAimPointRefresh = 0;
        return null;
    }

    /** @param entries Is expected to be sorted based on the priority (the highest value first) */
    private void selectTarget(final Projectile projectile, final ServerLevel level, final Collection<Homing.Mapped> entries) {
        Entity owner = projectile.getOwner();

        List<Entity> targets = level.getEntities(projectile, expandToRange(projectile), target -> {
            if (target == owner) {
                return false;
            }

            if (isInvalidTarget(target)) {
                return false;
            }

            //noinspection RedundantIfStatement -> keep for clarity
            if (isAllyToOwner(owner, target)) {
                return false;
            }

            return true;
        });

        if (targets.isEmpty()) {
            return;
        }

        sortByFocus(projectile, targets);

        for (Homing.Mapped homing : entries) {
            for (Entity target : targets) {
                if (AimPoint.findFreePoint(projectile, target) == null) {
                    continue;
                }

                if (homing.isValidTarget(level, projectile, target)) {
                    this.target = target;
                    targetId = target.getId();
                    velocityMultiplier = homing.velocityMultiplier();
                    maxTurnPerTick = homing.maxTurnPerTick();

                    aimPoint = null;
                    ticksUntilAimPointRefresh = 0;
                    blockedTicks = 0;
                    requiresSync = true;

                    break;
                }
            }

            if (targetId != NO_TARGET) {
                break;
            }
        }
    }

    /** Expands the bounding box of the projectile relative to the direction it's flying towards */
    private AABB expandToRange(final Projectile projectile) {
        Vec3 velocity = projectile.getDeltaMovement();
        // If the projectile is barely moving, its look angle is used as fallback
        // (Otherwise no expanding of the searchBox would happen)
        Vec3 front = velocity.lengthSqr() < EPSILON ? projectile.getLookAngle() : velocity.normalize();

        // Cross gives the perpendicular vector (90° of this vertical check -> right)
        Vec3 right = front.cross(UP).normalize();

        if (right == Vec3.ZERO) {
            // The projectile flies (nearly) straight up or down
            // Therefor use the direction as reference (not always used in case it is a rotating projectile)
            Direction facing = projectile.getDirection();
            Vec3 reference = Vec3.atLowerCornerOf(facing.getUnitVec3i());
            right = front.cross(reference);
        }

        Vec3 up = right.cross(front).normalize();

        // Negative values will increase min* and positive ones max*
        return projectile.getBoundingBox()
                .expandTowards(front.scale(maxSearchRange.front()))
                .expandTowards(front.scale(-maxSearchRange.back()))
                .expandTowards(right.scale(maxSearchRange.right()))
                .expandTowards(right.scale(-maxSearchRange.left()))
                .expandTowards(up.scale(maxSearchRange.up()))
                .expandTowards(up.scale(-maxSearchRange.down()));
    }

    /** Sorts the potential targets based on how much they are in the path of the projectile (the most focused entity first) */
    private void sortByFocus(final Projectile projectile, final List<Entity> targets) {
        if (targets.size() == 1) {
            return;
        }

        Vec3 velocity = projectile.getDeltaMovement();

        if (velocity.lengthSqr() < EPSILON) {
            return;
        }

        Vec3 direction = velocity.normalize();
        Vec3 position = projectile.position();

        targets.sort(Comparator.comparingDouble(target -> {
            Vec3 toTarget = position.vectorTo(target.getBoundingBox().getCenter());

            if (toTarget.lengthSqr() < EPSILON) {
                return -1;
            }

            // Negated since the entities with the smallest angle to the projectile are supposed to be first
            return -direction.dot(toTarget.normalize());
        }));
    }

    private boolean isAllyToOwner(@Nullable final Entity owner, final Entity target) {
        if (owner == null) {
            return false;
        }

        if (target.isAlliedTo(owner)) {
            return true;
        }

        if (!(owner instanceof LivingEntity livingOwner)) {
            return false;
        }

        if (target instanceof LivingEntity livingTarget) {
            if (!livingOwner.canAttack(livingTarget)) {
                return true;
            }

            if (!canAttackPlayer(owner, livingTarget)) {
                return true;
            }
        }

        if (target instanceof TamableAnimal tamable) {
            if (tamable.isOwnedBy(livingOwner)) {
                return true;
            }

            //noinspection RedundantIfStatement -> keep for clarity
            if (canAttackPlayer(owner, tamable.getOwner())) {
                return true;
            }
        }

        return false;
    }

    private boolean canAttackPlayer(final Entity owner, @Nullable final LivingEntity target) {
        if (!(owner instanceof Player player)) {
            return true;
        }

        if (!(target instanceof Player playerTarget)) {
            return true;
        }

        return player.canHarmPlayer(playerTarget);
    }

    /** Will also be checked for an already picked target */
    private boolean isInvalidTarget(final Entity target) {
        if (target == null) {
            return true;
        }

        if (target.isRemoved()) {
            return true;
        }

        if (target.isSpectator()) {
            return true;
        }

        if (target.isInvulnerable()) {
            return true;
        }

        if (target.isInvisible() && !target.isCurrentlyGlowing()) {
            // An entity going invisible should drop the homing
            return false;
        }

        if (target instanceof Player player && player.isCreative()) {
            return true;
        }

        return target instanceof LivingEntity livingTarget && livingTarget.isDeadOrDying();
    }

    private void clearTarget() {
        target = null;
        targetId = NO_TARGET;
        velocityMultiplier = NO_VELOCITY_MULTIPLIER;
        maxTurnPerTick = NO_MAX_TURN;
        aimPoint = null;
        ticksUntilAimPointRefresh = 0;
        blockedTicks = 0;
    }

    private static boolean isInvalidProjectile(final Projectile projectile) {
        if (projectile instanceof AbstractArrowAccess arrow && arrow.additional_enchantments$isInGround()) {
            return true;
        }

        if (projectile instanceof TridentAccess trident && trident.additional_enchantments$didDealDamage()) {
            return true;
        }

        return projectile.isRemoved();
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", Homing.Mapped.CODEC.listOf(), entries.values().stream().toList());
        output.store("max_range", HomingRange.Mapped.CODEC, maxSearchRange);
        output.putInt("target_id", targetId);
        output.putFloat("velocity_multiplier", velocityMultiplier);
        output.putFloat("max_turn_per_tick", maxTurnPerTick);
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", Homing.Mapped.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
        maxSearchRange = input.read("max_range", HomingRange.Mapped.CODEC).orElse(HomingRange.Mapped.NONE);
        targetId = input.getIntOr("target_id", NO_TARGET);
        velocityMultiplier = input.getFloatOr("velocity_multiplier", NO_VELOCITY_MULTIPLIER);
        maxTurnPerTick = input.getFloatOr("max_turn_per_tick", NO_MAX_TURN);
    }
}
