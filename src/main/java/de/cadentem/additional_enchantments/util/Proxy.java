package de.cadentem.additional_enchantments.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Proxy {
    default float getTimer() {
        return 1;
    }

    default void setPerceptionEntries(final Map<Integer, ShiftingColor.Mapped> entries) { /* Nothing to do */ }

    default void clearPerceptionEntries() { /* Nothing to do */ }

    default ShiftingColor.Mapped getPerceptionColor(final Entity entity) {
        return ShiftingColor.Mapped.NONE;
    }

    default @Nullable Player getLocalPlayer() {
        return null;
    }

    default void requestCacheClear() { /* Nothing to do */ }
}
