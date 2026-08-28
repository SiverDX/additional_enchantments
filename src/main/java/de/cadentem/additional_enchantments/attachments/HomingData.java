package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.homing.Homing;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class HomingData implements INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, Homing.Mapped> entries = new HashMap<>();

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
            Map<ResourceLocation, Homing.Mapped> entries = new HashMap<>();

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

    public void removeHoming(final Collection<ResourceLocation> ids) {
        ids.forEach(entries::remove);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

         Homing.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), entries.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("data", data));

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        entries.clear();

        Homing.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("data"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
