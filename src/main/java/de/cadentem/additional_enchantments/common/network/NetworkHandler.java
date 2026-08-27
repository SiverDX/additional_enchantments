package de.cadentem.additional_enchantments.common.network;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(SyncTreasureFinder.TYPE, SyncTreasureFinder.STREAM_CODEC, SyncTreasureFinder::handleClient);
        registrar.playToClient(SyncClimbable.TYPE, SyncClimbable.STREAM_CODEC, SyncClimbable::handleClient);
        registrar.playToClient(SyncPerception.TYPE, SyncPerception.STREAM_CODEC, SyncPerception::handleClient);
        registrar.playToClient(SyncFluidVision.TYPE, SyncFluidVision.STREAM_CODEC, SyncFluidVision::handleClient);

        registrar.playToClient(SyncLootTable.TYPE, SyncLootTable.STREAM_CODEC, SyncLootTable::handleClient);
        registrar.playToClient(SyncPerceptionEntries.TYPE, SyncPerceptionEntries.STREAM_CODEC, SyncPerceptionEntries::handleClient);
        registrar.playToClient(SyncClimbFlag.TYPE, SyncClimbFlag.STREAM_CODEC, SyncClimbFlag::handleClient);

        registrar.playBidirectional(SyncClimbablePositions.TYPE, SyncClimbablePositions.STREAM_CODEC, new DirectionalPayloadHandler<>(SyncClimbablePositions::handleClient, SyncClimbablePositions::handleServer));
    }

    public enum SyncType implements StringRepresentable {
        COMPLETE("complete"),
        ADD("add"),
        REMOVE("remove");

        public static final Codec<SyncType> CODEC = StringRepresentable.fromEnum(SyncType::values);
        private final String name;

        SyncType(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
