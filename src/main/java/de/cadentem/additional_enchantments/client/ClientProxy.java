package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.util.Proxy;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class ClientProxy implements Proxy {
    private @Unmodifiable Map</* Entity ID */ Integer, ShiftingColor.Mapped> entityPerceptionColors = Map.of();

    @Override
    public float getTimer() {
        return AEClient.TIMER;
    }

    @Override
    public void setPerceptionEntries(final Map<Integer, ShiftingColor.Mapped> entries) {
        this.entityPerceptionColors = entries;
    }

    @Override
    public void clearPerceptionEntries() {
        entityPerceptionColors = Map.of();
    }

    @Override
    public int getPerceptionColor(final Entity entity) {
        return entityPerceptionColors.getOrDefault(entity.getId(), ShiftingColor.Mapped.NONE).getColor();
    }

    @SubscribeEvent
    public static void clearData(final EntityLeaveLevelEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player) {
            AE.PROXY.clearPerceptionEntries();
        }
    }
}
