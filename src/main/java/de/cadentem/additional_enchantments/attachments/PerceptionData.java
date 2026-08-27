package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.common.network.SyncPerceptionEntries;
import de.cadentem.additional_enchantments.enchantments.perception.Perception;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class PerceptionData implements INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, Perception.Mapped> entries = new HashMap<>();

    private int maxRange;

    public ShiftingColor.Mapped getMappedColor(final ServerLevel serverLevel, final LivingEntity perceptionHolder, final Entity entity) {
        ShiftingColor.Mapped result = ShiftingColor.Mapped.NONE;

        for (Perception.Mapped perception : entries.values()) {
            ShiftingColor.Mapped color = perception.getColor(serverLevel, perceptionHolder, entity);

            if (color == ShiftingColor.Mapped.NONE) {
                continue;
            }

            if (result == ShiftingColor.Mapped.NONE || result.priority() < color.priority()) {
                result = color;
            }
        }

        return result;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void updateMaxRange() {
        int maxRange = 0;

        for (Perception.Mapped perception : entries.values()) {
            if (perception.range() > maxRange) {
                maxRange = perception.range();
            }
        }

        this.maxRange = maxRange * maxRange;
    }

    public Collection<Perception.Mapped> getEntries() {
        return entries.values();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void setEntries(final Collection<Perception.Mapped> entries) {
        this.entries.clear();
        addPerceptions(entries);
    }

    public void addPerceptions(final Collection<Perception.Mapped> perceptions) {
        perceptions.forEach(perception -> this.entries.put(perception.id(), perception));
        updateMaxRange();
    }

    public void removePerceptions(final Collection<ResourceLocation> ids) {
        ids.forEach(entries::remove);
        updateMaxRange();
    }

    @SubscribeEvent
    public static void collectEntries(final PlayerTickEvent.Pre event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        player.getExistingData(AEDataAttachments.PERCEPTION).ifPresent(data -> {
            AABB range = AABB.ofSize(player.position(), data.getMaxRange(), data.getMaxRange(), data.getMaxRange());
            Map<Integer, ShiftingColor.Mapped> perceptionEntries = new HashMap<>();

            serverPlayer.serverLevel().getEntities(player, range).forEach(entity -> {
                ShiftingColor.Mapped color = data.getMappedColor(serverPlayer.serverLevel(), player, entity);

                if (color != ShiftingColor.Mapped.NONE) {
                    perceptionEntries.put(entity.getId(), color);
                }
            });

            PacketDistributor.sendToPlayer(serverPlayer, new SyncPerceptionEntries(perceptionEntries));
        });
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        Perception.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), entries.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("data", data));

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        entries.clear();

        Perception.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("data"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
