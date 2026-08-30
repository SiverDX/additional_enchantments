package de.cadentem.additional_enchantments.common.network;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(SyncTreasureFinder.TYPE, SyncTreasureFinder.STREAM_CODEC);
        registrar.playToClient(SyncClimbable.TYPE, SyncClimbable.STREAM_CODEC);
        registrar.playToClient(SyncPerception.TYPE, SyncPerception.STREAM_CODEC);
        registrar.playToClient(SyncFluidVision.TYPE, SyncFluidVision.STREAM_CODEC);
        registrar.playToClient(SyncHoming.TYPE, SyncHoming.STREAM_CODEC);

        registrar.playToClient(SyncLootTable.TYPE, SyncLootTable.STREAM_CODEC);
        registrar.playToClient(SyncPerceptionEntries.TYPE, SyncPerceptionEntries.STREAM_CODEC);
        registrar.playToClient(SyncClimbFlag.TYPE, SyncClimbFlag.STREAM_CODEC);
        registrar.playToClient(SyncHomingProjectileData.TYPE, SyncHomingProjectileData.STREAM_CODEC);

        registrar.playBidirectional(SyncClimbablePositions.TYPE, SyncClimbablePositions.STREAM_CODEC, SyncClimbablePositions::handleServer);
    }

    @SubscribeEvent
    public static void register(final RegisterClientPayloadHandlersEvent event) {
        event.register(SyncTreasureFinder.TYPE, SyncTreasureFinder::handleClient);
        event.register(SyncClimbable.TYPE, SyncClimbable::handleClient);
        event.register(SyncPerception.TYPE, SyncPerception::handleClient);
        event.register(SyncFluidVision.TYPE, SyncFluidVision::handleClient);
        event.register(SyncHoming.TYPE, SyncHoming::handleClient);

        event.register(SyncLootTable.TYPE, SyncLootTable::handleClient);
        event.register(SyncPerceptionEntries.TYPE, SyncPerceptionEntries::handleClient);
        event.register(SyncClimbFlag.TYPE, SyncClimbFlag::handleClient);
        event.register(SyncHomingProjectileData.TYPE, SyncHomingProjectileData::handleClient);

        event.register(SyncClimbablePositions.TYPE, SyncClimbablePositions::handleClient);
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
