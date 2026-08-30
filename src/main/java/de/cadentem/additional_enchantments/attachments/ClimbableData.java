package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.common.network.SyncClimbFlag;
import de.cadentem.additional_enchantments.enchantments.climbing.Climbable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClimbableData implements ValueIOSerializable {
    // The core problem as to why this whole client / server setup is needed:
    // - The client calculates and stores the horizontal collision
    // - On the client-side the "blocking sliding down on ladders" part is handled
    // - Block predicates can only be evaluated on the server-side (i.e., is climbing allowed on that position)
    // Meaning the client needs to collect the relevant positions and the server has to approve them

    /** Temporarily kept to handle 'canStickToWalls' and ceiling climbing */
    public @Nullable BlockPos climbPosition;

    /** Retains whether the current {@link #climbPosition} was set from ceiling climbing */
    public boolean isCeilingClimbing;

    /**
     * Last set of (unfiltered in regard to climbable) positions collected by the client and sent to the server </br>
     * On the server-side they may be updated through the 'LevelMixin' (causing a refresh to be sent to the client)
     */
    public @Nullable @Unmodifiable Collection<BlockPos> trackedClimbPositions;

    /**
     * Client-only: positions the server has confirmed as climbable </br>
     * Used to actually check (on the client-side) whether climbing is allowed
     */
    private @Nullable @Unmodifiable Collection<BlockPos> approvedClimbPositions;

    /**
     * Purely for other players to know what the other client is doing </br>
     * On the server-side it is used to check whether a sync is required (i.e., type changed)
     */
    private SyncClimbFlag.ClimbingType climbingType = SyncClimbFlag.ClimbingType.NONE;

    private final Map<Identifier, Climbable> entries = new HashMap<>();

    public boolean isApprovedClimbPosition(final BlockPos position) {
        return approvedClimbPositions != null && approvedClimbPositions.contains(position);
    }

    public void setApprovedClimbPositions(@Unmodifiable final Collection<BlockPos> positions) {
        if (positions.isEmpty()) {
            approvedClimbPositions = null;
        } else {
            approvedClimbPositions = positions;
        }
    }

    public void setTrackedClimbPositions(@Unmodifiable final Collection<BlockPos> positions) {
        if (positions.isEmpty()) {
            trackedClimbPositions = null;
        } else {
            trackedClimbPositions = positions;
        }
    }

    public boolean canClimb(final WorldGenLevel level, final BlockPos position, final LivingEntity entity) {
        if (entries.isEmpty()) {
            return false;
        }

        boolean isCeiling = position.getY() > entity.getBlockY();

        for (final Climbable climbable : entries.values()) {
            if (isCeiling && !climbable.canClimbCeilings()) {
                continue;
            }

            if (climbable.canClimb(level, position)) {
                return true;
            }
        }

        return false;
    }

    public SyncClimbFlag.ClimbingType getClimbingType() {
        return climbingType;
    }

    public void setClimbingType(final SyncClimbFlag.ClimbingType climbingType) {
        this.climbingType = climbingType;
        isCeilingClimbing = climbingType == SyncClimbFlag.ClimbingType.CEILING;
    }

    public boolean isCeilingClimbing() {
        return climbPosition != null && isCeilingClimbing;
    }

    public boolean canClimbCeilings() {
        if (entries.isEmpty()) {
            return false;
        }

        for (final Climbable climbable : entries.values()) {
            if (climbable.canClimbCeilings()) {
                return true;
            }
        }

        return false;
    }

    public boolean canStickToWalls(final WorldGenLevel level) {
        if (entries.isEmpty() || climbPosition == null) {
            return false;
        }

        boolean isCeilingCandidate = isCeilingClimbing;

        for (final Climbable climbable : entries.values()) {
            if (isCeilingCandidate && !climbable.canClimbCeilings()) {
                continue;
            }

            if (climbable.canStickToWalls(level, climbPosition)) {
                return true;
            }
        }

        return false;
    }

    public Collection<Climbable> getEntries() {
        return entries.values();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void setEntries(final Collection<Climbable> entries) {
        this.entries.clear();
        addClimbables(entries);
    }

    public void addClimbables(final Collection<Climbable> climbables) {
        climbables.forEach(climbable -> this.entries.put(climbable.id(), climbable));
    }

    public void removeClimbables(final Collection<Identifier> ids) {
        ids.forEach(entries::remove);
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", Climbable.CODEC.listOf(), entries.values().stream().toList());
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", Climbable.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
