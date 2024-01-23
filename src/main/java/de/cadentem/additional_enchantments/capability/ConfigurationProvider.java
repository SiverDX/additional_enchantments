package de.cadentem.additional_enchantments.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Map<String, LazyOptional<Configuration>> SERVER_CACHE = new HashMap<>();
    public static final Map<String, LazyOptional<Configuration>> CLIENT_CACHE = new HashMap<>();

    private final Configuration handler;
    private final LazyOptional<Configuration> instance;

    public ConfigurationProvider() {
        handler = new Configuration();
        instance = LazyOptional.of(() -> handler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.CONFIGURATION_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    public static LazyOptional<Configuration> getCapability(final Entity entity) {
        if (entity instanceof Player) {
            Map<String, LazyOptional<Configuration>> sidedCache = entity.getLevel().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;

            return sidedCache.computeIfAbsent(entity.getStringUUID(), key -> {
                LazyOptional<Configuration> capability = entity.getCapability(CapabilityHandler.CONFIGURATION_CAPABILITY);
                capability.addListener(ignored -> sidedCache.remove(entity.getStringUUID()));
                return capability;
            });
        }

        return LazyOptional.empty();
    }
}
