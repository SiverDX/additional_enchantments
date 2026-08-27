package de.cadentem.additional_enchantments.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(SyncBlockVision.TYPE, SyncBlockVision.STREAM_CODEC, SyncBlockVision::handleClient);
        registrar.playToClient(SyncClimbable.TYPE, SyncClimbable.STREAM_CODEC, SyncClimbable::handleClient);
        registrar.playToClient(SyncPerception.TYPE, SyncPerception.STREAM_CODEC, SyncPerception::handleClient);
        registrar.playToClient(SyncFluidVision.TYPE, SyncFluidVision.STREAM_CODEC, SyncFluidVision::handleClient);

        registrar.playToClient(SyncLootTable.TYPE, SyncLootTable.STREAM_CODEC, SyncLootTable::handleClient);
        registrar.playToClient(SyncPerceptionEntries.TYPE, SyncPerceptionEntries.STREAM_CODEC, SyncPerceptionEntries::handleClient);
        registrar.playToClient(SyncClimbFlag.TYPE, SyncClimbFlag.STREAM_CODEC, SyncClimbFlag::handleClient);

        registrar.playBidirectional(SyncClimbablePositions.TYPE, SyncClimbablePositions.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncClimbablePositions::handleClient, SyncClimbablePositions::handleServer));
    }
}
