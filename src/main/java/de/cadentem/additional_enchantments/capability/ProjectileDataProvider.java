package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.client.ClientProxy;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProjectileDataProvider implements ICapabilitySerializable<CompoundTag>  {
    public static final Map<String, LazyOptional<ProjectileData>> SERVER_CACHE = new HashMap<>();
    public static final Map<String, LazyOptional<ProjectileData>> CLIENT_CACHE = new HashMap<>();

    private final ProjectileData handler;
    private final LazyOptional<ProjectileData> instance;

    public ProjectileDataProvider() {
        handler = new ProjectileData();
        instance = LazyOptional.of(() -> handler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.PROJECTILE_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    public static LazyOptional<ProjectileData> getCapability(final Entity entity) {
        if (entity instanceof Projectile) {
            Map<String, LazyOptional<ProjectileData>> sidedCache = entity.getLevel().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;

            return sidedCache.computeIfAbsent(entity.getStringUUID(), key -> {
                LazyOptional<ProjectileData> capability = entity.getCapability(CapabilityHandler.PROJECTILE_DATA_CAPABILITY);
                capability.addListener(ignored -> sidedCache.remove(key));
                return capability;
            });
        }

        return LazyOptional.empty();
    }

    public static void removeCachedEntry(final Entity entity) {
        if (entity.getLevel().isClientSide()) {
            if (entity == ClientProxy.getLocalPlayer()) {
                CLIENT_CACHE.clear(); // TODO :: find other way to clear cache when player leaves a world (this also triggers for death)
            } else {
                CLIENT_CACHE.remove(entity.getStringUUID());
            }
        } else {
            SERVER_CACHE.remove(entity.getStringUUID());
        }
    }
}
