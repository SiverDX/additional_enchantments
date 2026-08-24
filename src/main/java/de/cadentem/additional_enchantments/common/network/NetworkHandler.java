package de.cadentem.additional_enchantments.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(SyncBlockVision.TYPE, SyncBlockVision.STREAM_CODEC, SyncBlockVision::handleClient);
        registrar.playToClient(SyncLootTable.TYPE, SyncLootTable.STREAM_CODEC, SyncLootTable::handleClient);
    }
}
