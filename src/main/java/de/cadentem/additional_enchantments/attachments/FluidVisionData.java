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

import java.util.HashMap;
import java.util.Map;

public class FluidVisionData implements INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, FluidVision.Mapped> fluids = new HashMap<>();

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

        for (final FluidVision.Mapped entry : fluids.values()) {
            if (entry.fluidTypes().contains(fluidType)) {
                return entry;
            }
        }

        return FluidVision.Mapped.NONE;
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

        FluidVision.Mapped.CODEC.listOf().encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), fluids.values().stream().toList())
                .resultOrPartial(AE.LOG::error)
                .ifPresent(data -> tag.put("data", data));

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        fluids.clear();

        FluidVision.Mapped.CODEC.listOf().parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get("data"))
                .resultOrPartial(AE.LOG::error)
                .ifPresent(entries -> entries.forEach(entry -> fluids.put(entry.id(), entry)));
    }
}
