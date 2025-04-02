package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.client.ClientProxy;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PerceptionEnchantment extends ConfigurableEnchantment {
    private static final Map<String, Pair<Integer, Integer>> CLIENT_CACHE = new HashMap<>();

    public enum DisplayType {
        ALL,
        NO_ITEMS,
        NONE
    }

    public PerceptionEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD, AEEnchantments.PERCEPTION_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        if (other == AEEnchantments.ORE_SIGHT.get() || other == AEEnchantments.TREASURE_FINDER.get()) {
            return false;
        }

        return super.checkCompatibility(other);
    }

    public static int getClientEnchantmentLevel() {
        Player localPlayer = ClientProxy.getLocalPlayer();

        if (localPlayer == null) {
            return 0;
        }

        Pair<Integer, Integer> data = CLIENT_CACHE.get(localPlayer.getStringUUID());

        // Cache is being kept even when the player leaves
        if (data == null || Mth.abs(localPlayer.tickCount - data.first()) > 20) {
            data = Pair.of(localPlayer.tickCount, EnchantmentHelper.getEnchantmentLevel(AEEnchantments.PERCEPTION.get(), localPlayer));
            CLIENT_CACHE.put(localPlayer.getStringUUID(), data);
        }

        return data.second();
    }
}
