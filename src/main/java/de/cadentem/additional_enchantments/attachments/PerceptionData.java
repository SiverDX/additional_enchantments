package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.common.network.SyncPerceptionEntries;
import de.cadentem.additional_enchantments.enchantments.perception.Perception;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class PerceptionData implements ValueIOSerializable {
    private final Map<Identifier, Perception.Mapped> entries = new HashMap<>();

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

    public void removePerceptions(final Collection<Identifier> ids) {
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

            serverPlayer.level().getEntities(player, range).forEach(entity -> {
                ShiftingColor.Mapped color = data.getMappedColor(serverPlayer.level(), player, entity);

                if (color != ShiftingColor.Mapped.NONE) {
                    perceptionEntries.put(entity.getId(), color);
                }
            });

            PacketDistributor.sendToPlayer(serverPlayer, new SyncPerceptionEntries(perceptionEntries));
        });
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", Perception.Mapped.CODEC.listOf(), entries.values().stream().toList());
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", Perception.Mapped.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
