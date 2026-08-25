package de.cadentem.additional_enchantments.util;

import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface Proxy {
    default float getTimer() {
        return 1;
    }

    default void addPerceptionEntry(final Entity... entities) { /* Nothing to do */ }

    default void removePerceptionEntry(final Entity... entities) { /* Nothing to do */ }

    default int getPerceptionColor(final Entity entity) {
        return Colors.NONE;
    }

    // TODO :: check if needed
    default void clearPerceptionEntries() { /* Nothing to do */ };

    default void setPerceptionEntries(final Map<Integer, ShiftingColor.Mapped> entries) { /* Nothing to do */ };
}
