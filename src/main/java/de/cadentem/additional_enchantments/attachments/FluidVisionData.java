package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVision;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FluidVisionData implements INBTSerializable<CompoundTag> {
    public boolean pendingVisionUpdate;

    // TODO :: add cache for fluidtype : holder?
    private final Map<ResourceLocation, FluidVision.Mapped> entries = new HashMap<>();

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

    public void removeVisions(final Collection<ResourceLocation> ids) {
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
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        FluidVision.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), entries.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("data", data));

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        entries.clear();

        FluidVision.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("data"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> this.entries.put(entry.id(), entry)));
    }
}
