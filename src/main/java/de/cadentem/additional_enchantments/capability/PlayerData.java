package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.enchantments.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Explosion;

public class PlayerData {
    public MobEffectCategory effectFilter = MobEffectCategory.HARMFUL;
    public Explosion.BlockInteraction explosionType = Explosion.BlockInteraction.DESTROY;
    public HomingEnchantment.TypeFilter homingTypeFilter = HomingEnchantment.TypeFilter.ANY;
    public HomingEnchantment.Priority homingPriority = HomingEnchantment.Priority.CLOSEST;
    public PerceptionEnchantment.DisplayType displayType = PerceptionEnchantment.DisplayType.ALL;
    public Rarity itemFilter = Rarity.COMMON;
    public OreSightEnchantment.OreRarity oreRarity = OreSightEnchantment.OreRarity.ALL;
    public VoidingEnchantment.State voidingState = VoidingEnchantment.State.ENABLED;

    // Not synced but updated on both sides, otherwise 1 packet per tick would be needed
    public int hunterStacks;

    // Server only
    public boolean isOnHunterBlock;

    public void cycleEffectFilter() {
        effectFilter = (MobEffectCategory) cycle(effectFilter);
    }

    public void cycleExplosionType() {
        // DESTROY decays the loot (reduces the stack size of dropped items)
        if (explosionType == Explosion.BlockInteraction.DESTROY) {
            explosionType = Explosion.BlockInteraction.KEEP;
        } else {
            explosionType = Explosion.BlockInteraction.DESTROY;
        }
    }

    public void cycleHomingFilter() {
        homingTypeFilter = (HomingEnchantment.TypeFilter) cycle(homingTypeFilter);
    }

    public void cycleHomingPriority() {
        homingPriority = (HomingEnchantment.Priority) cycle(homingPriority);
    }

    public void cycleDisplayType() {
        displayType = (PerceptionEnchantment.DisplayType) cycle(displayType);
    }

    public void cycleItemFilter() {
        itemFilter = (Rarity) cycle(itemFilter);
    }

    public void cycleOreRarity() {
        oreRarity = (OreSightEnchantment.OreRarity) cycle(oreRarity);
    }

    public void cycleVoiding() {
        voidingState = (VoidingEnchantment.State) cycle(voidingState);
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

    public void increaseHunterStacks(int enchantmentLevel) {
        hunterStacks = Math.min(HunterEnchantment.getMaxStacks(enchantmentLevel), hunterStacks + 1);
    }

    public void reduceHunterStacks(final LivingEntity livingEntity, int enchantmentLevel) {
        if (livingEntity.tickCount % Math.max(1, enchantmentLevel / ServerConfig.HUNTER_STACK_REDUCTION.get()) == 0) {
            hunterStacks = Math.max(0, hunterStacks - 1);
        }
    }

    public boolean hasMaxHunterStacks(int enchantmentLevel) {
        return hunterStacks >= HunterEnchantment.getMaxStacks(enchantmentLevel);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("effectFilter", effectFilter.ordinal());
        tag.putInt("explosionType", explosionType.ordinal());
        tag.putInt("homingTypeFilter", homingTypeFilter.ordinal());
        tag.putInt("homingPriority", homingPriority.ordinal());
        tag.putInt("itemFilter", itemFilter.ordinal());
        tag.putInt("oreRarity", oreRarity.ordinal());
        tag.putInt("voidingState", voidingState.ordinal());

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        effectFilter = MobEffectCategory.values()[tag.getInt("effectFilter")];
        explosionType = Explosion.BlockInteraction.values()[tag.getInt("explosionType")];
        homingTypeFilter = HomingEnchantment.TypeFilter.values()[tag.getInt("homingTypeFilter")];
        homingPriority = HomingEnchantment.Priority.values()[tag.getInt("homingPriority")];
        itemFilter = Rarity.values()[tag.getInt("itemFilter")];
        oreRarity = OreSightEnchantment.OreRarity.values()[tag.getInt("oreRarity")];
        voidingState = VoidingEnchantment.State.values()[tag.getInt("voidingState")];
    }
}
