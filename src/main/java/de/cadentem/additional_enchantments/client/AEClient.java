package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AE.MODID, dist = Dist.CLIENT)
public class AEClient {
    public static float TIMER;

    private static final float TIMER_INCREMENT = 0.01f;

    public AEClient(final IEventBus eventBus, final ModContainer container) {
        NeoForge.EVENT_BUS.addListener(this::incrementTimer);
    }

    private void incrementTimer(final ClientTickEvent.Post event) {
        if (TIMER + TIMER_INCREMENT > Float.MAX_VALUE) {
            TIMER = 0;
        } else {
            TIMER += TIMER_INCREMENT;
        }
    }
}
