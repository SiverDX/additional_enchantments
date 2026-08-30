package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.enchantments.homing.Homing;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class HomingData implements ValueIOSerializable {
    private final Map<Identifier, Homing.Mapped> entries = new HashMap<>();

    @SubscribeEvent
    public static void attachData(final EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile projectile)) {
            return;
        }

        if (projectile.level().isClientSide()) {
            return;
        }

        Entity owner = projectile.getOwner();

        if (owner == null) {
            return;
        }

        owner.getExistingData(AEDataAttachments.HOMING).ifPresent(data -> {
            Map<Identifier, Homing.Mapped> entries = new HashMap<>();

            for (Homing.Mapped homing : data.entries.values()) {
                if (homing.isValidForProjectile(projectile)) {
                    entries.put(homing.id(), homing);
                }
            }

            projectile.getData(AEDataAttachments.PROJECTILE_HOMING_DATA).setEntries(entries);
        });
    }

    public Collection<Homing.Mapped> getEntries() {
        return entries.values();
    }

    public void setEntries(final Collection<Homing.Mapped> entries) {
        this.entries.clear();
        addHoming(entries);
    }

    public void addHoming(final Collection<Homing.Mapped> visions) {
        visions.forEach(perception -> this.entries.put(perception.id(), perception));
    }

    public void removeHoming(final Collection<Identifier> ids) {
        ids.forEach(entries::remove);
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", Homing.Mapped.CODEC.listOf(), entries.values().stream().toList());
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", Homing.Mapped.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
