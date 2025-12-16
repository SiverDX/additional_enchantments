package de.cadentem.additional_enchantments.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AEClient {
    private static final float TIMER_INCREMENT = 0.01f;

    public static float timer;

    @SubscribeEvent
    public static void incrementTimer(final TickEvent.ClientTickEvent event) {
        if (timer + TIMER_INCREMENT > Float.MAX_VALUE) {
            timer = 0;
        } else {
            timer += TIMER_INCREMENT;
        }
    }
}
