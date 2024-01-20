package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.enchantments.config.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.config.EnchantmentCategories;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ShatterEnchantment extends ConfigurableEnchantment {
    public ShatterEnchantment() {
        super(Rarity.RARE, EnchantmentCategories.RANGED, EquipmentSlot.MAINHAND, AEEnchantments.SHATTER_ID);
    }

    @SubscribeEvent
    public static void handleAmmo(final LivingGetProjectileEvent event) {
        int level = event.getProjectileWeaponItemStack().getEnchantmentLevel(AEEnchantments.SHATTER.get());

        if (level > 0) {
            if (event.getEntity() instanceof Player player && !player.isCreative()) {
                int slot = player.getInventory().findSlotMatchingItem(Items.AMETHYST_SHARD.getDefaultInstance());

                if (slot == -1) {
                    return;
                }

                player.getInventory().getItem(slot).shrink(1);
                event.setProjectileItemStack(createAmmo());

                return;
            }

            event.setProjectileItemStack(createAmmo());
        }
    }

    private static ItemStack createAmmo() {
        ItemStack stack = new ItemStack(AEItems.SHARD_ARROW.get());
        stack.setCount(stack.getMaxStackSize());
        return stack;
    }
}
