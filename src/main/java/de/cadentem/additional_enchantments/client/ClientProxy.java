package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.util.Proxy;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public class ClientProxy implements Proxy {
    private @Unmodifiable Map</* Entity ID */ Integer, ShiftingColor.Mapped> entityPerceptionColors = Map.of();

    @Override
    public float getTimer() {
        return AEClient.TIMER;
    }

    @Override // TODO :: check if the update per tick is enough to not clutter it
    public void setPerceptionEntries(final Map<Integer, ShiftingColor.Mapped> entries) {
        this.entityPerceptionColors = entries;
    }

    public int getPerceptionColor(final Entity entity) {
        return entityPerceptionColors.getOrDefault(entity.getId(), ShiftingColor.Mapped.NONE).getColor();
    }
}
