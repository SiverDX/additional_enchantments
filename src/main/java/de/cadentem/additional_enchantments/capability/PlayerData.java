package de.cadentem.additional_enchantments.capability;

import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.enchantments.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Explosion;

public class PlayerData {
    public MobEffectCategory effectFilter = MobEffectCategory.HARMFUL;
    public Explosion.BlockInteraction explosionType = Explosion.BlockInteraction.BREAK;
    public HomingEnchantment.TypeFilter homingTypeFilter = HomingEnchantment.TypeFilter.ANY;
    public HomingEnchantment.Priority homingPriority = HomingEnchantment.Priority.CLOSEST;
    public PerceptionEnchantment.DisplayType displayType = PerceptionEnchantment.DisplayType.ALL;
    public Rarity itemFilter = Rarity.COMMON;
    public OreSightEnchantment.OreRarity oreRarity = OreSightEnchantment.OreRarity.ALL;
    public VoidingEnchantment.State voidingState = VoidingEnchantment.State.ENABLED;

    // Not synced
    private int hunterStacks;

    // Server only
    public boolean isOnHunterBlock;

    // Client only
    private int delayStacks;

    public void cycleEffectFilter() {
        effectFilter = (MobEffectCategory) cycle(effectFilter);
    }

    public void cycleExplosionType() {
        // DESTROY decays the loot (reduces the stack size of dropped items)
        if (explosionType == Explosion.BlockInteraction.BREAK) {
            explosionType = Explosion.BlockInteraction.NONE;
        } else {
            explosionType = Explosion.BlockInteraction.BREAK;
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

    public boolean hasHunterStacks() {
        if (hunterStacks > 0) {
            return true;
        }

        return delayStacks > 0;
    }

    public int getHunterStacks() {
        return hunterStacks;
    }

    public void clearHunterStacks(final Player player) {
        hunterStacks = 0;
        setDelayStacks(player);
    }

    public void increaseHunterStacks(final Player player, int enchantmentLevel) {
        if (!player.getLevel().isClientSide()) {
            delayStacks = 10;
            CapabilityHandler.syncPlayerData(player);
        }

        hunterStacks = Math.min(HunterEnchantment.getMaxStacks(enchantmentLevel), hunterStacks + 1);
    }

    public void reduceHunterStacks(final Player player, int enchantmentLevel) {
        if (hunterStacks > 0 && player.tickCount % Math.max(1, enchantmentLevel / ServerConfig.HUNTER_STACK_REDUCTION.get()) == 0) {
            hunterStacks = Math.max(0, hunterStacks - 1);
            setDelayStacks(player);
        }
    }

    public boolean hasMaxHunterStacks(int enchantmentLevel) {
        return hunterStacks >= HunterEnchantment.getMaxStacks(enchantmentLevel);
    }

    public void reduceDelayStacks() {
        delayStacks = Math.max(0, delayStacks - 1);
    }

    private void setDelayStacks(final Player player) {
        if (hunterStacks == 0 && player.getLevel().isClientSide()) {
            delayStacks = 10;
        }
    }

    public CompoundTag serializeNBT(boolean onlyPersistent) {
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
