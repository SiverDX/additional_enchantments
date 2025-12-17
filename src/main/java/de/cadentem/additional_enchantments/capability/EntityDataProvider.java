package de.cadentem.additional_enchantments.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Map<Integer, LazyOptional<EntityData>> SERVER_CACHE = new HashMap<>();

    private final EntityData data = new EntityData();
    private final LazyOptional<EntityData> instance = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.ENTITY_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    public static LazyOptional<EntityData> getCapability(final Entity entity) {
        if (entity.level().isClientSide()) {
            return LazyOptional.empty();
        }

        if (entity instanceof LivingEntity) {
            LazyOptional<EntityData> capability = SERVER_CACHE.get(entity.getId());

            if (capability == null) {
                capability = entity.getCapability(CapabilityHandler.ENTITY_DATA_CAPABILITY);
                capability.addListener(ignored -> SERVER_CACHE.remove(entity.getId()));

                if (capability.isPresent()) {
                    SERVER_CACHE.put(entity.getId(), capability);
                }
            }

            return capability;
        }

        return LazyOptional.empty();
    }

    public static void removeCachedEntry(final LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            SERVER_CACHE.remove(entity.getId());
        }
    }
}
