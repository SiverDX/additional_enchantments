package de.cadentem.additional_enchantments.util;

import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface Proxy {
    default float getTimer() {
        return 1;
    }

    default void setPerceptionEntries(final Map<Integer, ShiftingColor.Mapped> entries) { /* Nothing to do */ }

    default void clearPerceptionEntries() { /* Nothing to do */ }

    default int getPerceptionColor(final Entity entity) {
        return Colors.NONE;
    }
}
