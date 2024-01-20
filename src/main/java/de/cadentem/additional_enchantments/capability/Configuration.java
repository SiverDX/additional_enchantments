package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Explosion;

public class Configuration {
    public MobEffectCategory effectFilter = MobEffectCategory.HARMFUL;
    public Explosion.BlockInteraction explosionType = Explosion.BlockInteraction.DESTROY;
    public HomingEnchantment.TypeFilter homingTypeFilter = HomingEnchantment.TypeFilter.ANY;
    public HomingEnchantment.Priority homingPriority = HomingEnchantment.Priority.CLOSEST;

    public void cycleEffectFilter() {
        effectFilter = (MobEffectCategory) cycle(effectFilter);
    }

    public void cycleExplosionType() {
        explosionType = (Explosion.BlockInteraction) cycle(explosionType);
    }

    public void cycleHomingFilter() {
        homingTypeFilter = (HomingEnchantment.TypeFilter) cycle(homingTypeFilter);
    }

    public void cycleHomingPriority() {
        homingPriority = (HomingEnchantment.Priority) cycle(homingPriority);
    }

    private Enum<?> cycle(final Enum<?> type) {
        int ordinal = type.ordinal();

        Class<?> declaringClass = type.getDeclaringClass();
        Object[] values = declaringClass.getEnumConstants();

        if (ordinal == values.length - 1) {
            ordinal = 0;
        } else {
            ordinal++;
        }

        return (Enum<?>) values[ordinal];
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("effectFilter", effectFilter.ordinal());
        tag.putInt("explosionType", explosionType.ordinal());
        tag.putInt("homingTypeFilter", homingTypeFilter.ordinal());
        tag.putInt("homingPriority", homingPriority.ordinal());

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        effectFilter = MobEffectCategory.values()[tag.getInt("effectFilter")];
        explosionType = Explosion.BlockInteraction.values()[tag.getInt("explosionType")];
        homingTypeFilter = HomingEnchantment.TypeFilter.values()[tag.getInt("homingTypeFilter")];
        homingPriority = HomingEnchantment.Priority.values()[tag.getInt("homingPriority")];
    }
}
