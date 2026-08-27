package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.common.network.NetworkHandler;
import de.cadentem.additional_enchantments.common.network.SyncClimbable;
import de.cadentem.additional_enchantments.common.network.SyncFluidVision;
import de.cadentem.additional_enchantments.common.network.SyncPerception;
import de.cadentem.additional_enchantments.common.network.SyncTreasureFinder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber
public class AEDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, AE.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TreasureFinderData>> TREASURE_FINDER = REGISTRY.register("treasure_finder", () -> AttachmentType.serializable(TreasureFinderData::new).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ClimbableData>> CLIMBABLE = REGISTRY.register("climbable", () -> AttachmentType.serializable(ClimbableData::new).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PerceptionData>> PERCEPTION = REGISTRY.register("perception", () -> AttachmentType.serializable(PerceptionData::new).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FluidVisionData>> FLUID_VISION = REGISTRY.register("fluid_vision", () -> AttachmentType.serializable(FluidVisionData::new).copyOnDeath().build());

    @SubscribeEvent
    public static void syncOnDimensionChange(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        syncAll(serverPlayer);
    }

    @SubscribeEvent
    public static void syncOnClone(final PlayerEvent.Clone event) {
        if (!(event.getOriginal() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        syncAll(serverPlayer);
    }

    private static void syncAll(final ServerPlayer serverPlayer) {
        serverPlayer.getExistingData(AEDataAttachments.TREASURE_FINDER).ifPresent(data -> {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncTreasureFinder(data.getEntries().stream().toList(), NetworkHandler.SyncType.COMPLETE));
        });

        serverPlayer.getExistingData(AEDataAttachments.CLIMBABLE).ifPresent(data -> {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncClimbable(data.getEntries().stream().toList(), NetworkHandler.SyncType.COMPLETE));
        });

        serverPlayer.getExistingData(AEDataAttachments.PERCEPTION).ifPresent(data -> {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncPerception(data.getEntries().stream().toList(), NetworkHandler.SyncType.COMPLETE));
        });

        serverPlayer.getExistingData(AEDataAttachments.FLUID_VISION).ifPresent(data -> {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncFluidVision(data.getEntries().stream().toList(), NetworkHandler.SyncType.COMPLETE));
        });
    }
}
