package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FluidVisionData implements ValueIOSerializable {
    public boolean pendingVisionUpdate;

    // TODO :: add cache for fluidtype : holder?
    private final Map<Identifier, FluidVision.Mapped> entries = new HashMap<>();

    public FluidVision.Mapped get(final FluidType type, final RegistryAccess access) {
        Holder<FluidType> holder = holder(type, access);

        if (holder == null) {
            return FluidVision.Mapped.NONE;
        }

        return get(holder);
    }

    public FluidVision.Mapped get(final Holder<FluidType> fluidType) {
        if (fluidType == null) {
            return FluidVision.Mapped.NONE;
        }

        for (final FluidVision.Mapped entry : entries.values()) {
            if (entry.fluidTypes().contains(fluidType)) {
                return entry;
            }
        }

        return FluidVision.Mapped.NONE;
    }

    public Collection<FluidVision.Mapped> getEntries() {
        return entries.values();
    }

    public void setEntries(final Collection<FluidVision.Mapped> entries) {
        this.entries.clear();
        addVisions(entries);
    }

    public void addVisions(final Collection<FluidVision.Mapped> visions) {
        visions.forEach(perception -> this.entries.put(perception.id(), perception));
        pendingVisionUpdate = true;
    }

    public void removeVisions(final Collection<Identifier> ids) {
        ids.forEach(entries::remove);
        pendingVisionUpdate = true;
    }

    private static @Nullable Holder<FluidType> holder(final FluidType fluid, final RegistryAccess access) {
        ResourceKey<FluidType> key = NeoForgeRegistries.FLUID_TYPES.getResourceKey(fluid).orElse(null);

        if (key == null) {
            return null;
        }

        return access.lookupOrThrow(NeoForgeRegistries.FLUID_TYPES.key()).get(key).orElse(null);
    }

    @Override
    public void serialize(@NotNull final ValueOutput output) {
        output.store("entries", FluidVision.Mapped.CODEC.listOf(), entries.values().stream().toList());
    }

    @Override
    public void deserialize(@NotNull final ValueInput input) {
        entries.clear();
        input.read("entries", FluidVision.Mapped.CODEC.listOf()).ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
