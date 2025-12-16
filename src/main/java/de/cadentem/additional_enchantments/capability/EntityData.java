package de.cadentem.additional_enchantments.capability;

import net.minecraft.nbt.CompoundTag;

public class EntityData {
    public int tippedCooldown;

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tipped_cooldown", tippedCooldown);
        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        tippedCooldown = tag.getInt("tipped_cooldown");
    }
}
