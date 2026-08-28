package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.common.network.SyncHomingProjectileData;
import de.cadentem.additional_enchantments.enchantments.homing.Homing;
import de.cadentem.additional_enchantments.mixin.AbstractArrowAccess;
import de.cadentem.additional_enchantments.mixin.TridentAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
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
public class ProjectileHomingData implements INBTSerializable<CompoundTag> {
    private static final int NO_TARGET = -1;
    private static final int NO_VELOCITY_MULTIPLIER = 1;

    /** Only required server-side */
    private final Map<ResourceLocation, Homing.Mapped> entries = new HashMap<>();

    /** Transient data */
    private Entity target;
    private int targetId = NO_TARGET;
    private float velocityMultiplier = NO_VELOCITY_MULTIPLIER;
    private int maxRange;

    private boolean requiresSync;

    @SubscribeEvent
    public static void handleHoming(final EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }

        projectile.getExistingData(AEDataAttachments.PROJECTILE_HOMING_DATA).ifPresent(data -> {
            if (ProjectileHomingData.isInvalidProjectile(projectile)) {
                data.target = null;
                data.targetId = NO_TARGET;
                data.velocityMultiplier = NO_VELOCITY_MULTIPLIER;
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

    public void setEntries(final Map<ResourceLocation, Homing.Mapped> entries) {
        this.entries.clear();
        this.entries.putAll(entries);

        for (Homing.Mapped homing : this.entries.values()) {
            if (maxRange < homing.range()) {
                maxRange = homing.range();
            }
        }
    }

    public void setClientData(final Entity target, int targetId, float velocityMultiplier) {
        this.target = target;
        this.targetId = targetId;
        this.velocityMultiplier = velocityMultiplier;
    }

    public void syncToClient(final Projectile projectile) {
        if (projectile.level().isClientSide()) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(projectile, new SyncHomingProjectileData(projectile.getId(), targetId, velocityMultiplier));
        requiresSync = false;
    }

    private void handleTarget(final Projectile projectile, final Collection<Homing.Mapped> entries) {
        if (target == null && targetId != NO_TARGET) {
            target = projectile.level().getEntity(targetId);
        }

        if (isInvalidTarget(target)) {
            target = null;
            targetId = NO_TARGET;
            requiresSync = true;
        }

        if (targetId == NO_TARGET && projectile.level() instanceof ServerLevel serverLevel) {
            selectTarget(projectile, serverLevel, entries.stream().sorted(Comparator.comparingInt(Homing.Mapped::priority).reversed()).toList());
        }

        if (target == null) {
            return;
        }

        Vec3 velocity = projectile.getDeltaMovement();
        Vec3 motion = projectile.position().vectorTo(target.position().add(0, target.getEyeHeight() / 2, 0));
        motion = motion.normalize().scale(velocity.length() * velocityMultiplier);
        projectile.setDeltaMovement(motion);
    }

    /** @param entries Is expected to be sorted based on the priority (the highest value first) */
    private void selectTarget(final Projectile projectile, final ServerLevel level, final Collection<Homing.Mapped> entries) {
        Entity owner = projectile.getOwner();

        List<Entity> targets = level.getEntities(projectile, projectile.getBoundingBox().inflate(maxRange), target -> {
            if (target == owner) {
                return false;
            }

            if (isInvalidTarget(target)) {
                return false;
            }

            if (target.isInvulnerable() || (target.isInvisible() && !target.isCurrentlyGlowing())) {
                return false;
            }

            //noinspection RedundantIfStatement -> keep for clarity
            if (isAllyToOwner(owner, target)) {
                return false;
            }

            return true;
        });

        for (Homing.Mapped homing : entries) {
            for (Entity target : targets) {
                if (homing.isValidTarget(level, projectile, target)) {
                    this.target = target;
                    targetId = target.getId();
                    velocityMultiplier = homing.velocityMultiplier();
                    requiresSync = true;
                    break;
                }
            }

            if (targetId != NO_TARGET) {
                break;
            }
        }
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

        return target instanceof TamableAnimal tamable && tamable.isOwnedBy(livingOwner);
    }

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

        if (target instanceof Player player && player.isCreative()) {
            return true;
        }

        return target instanceof LivingEntity livingTarget && livingTarget.isDeadOrDying();
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
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        Homing.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), entries.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("entries", data));

        tag.putInt("target_id", targetId);
        tag.putFloat("velocity_multiplier", velocityMultiplier);
        tag.putInt("max_range", maxRange);

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        entries.clear();

        Homing.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("entries"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));

        targetId = tag.contains("target_id") ? tag.getInt("target_id") : NO_TARGET;
        velocityMultiplier = tag.contains("velocity_multiplier") ? tag.getFloat("velocity_multiplier") : NO_VELOCITY_MULTIPLIER;
        maxRange = tag.getInt("max_range");
    }
}
