package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.core.entity.ShardArrow;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import de.cadentem.additional_enchantments.registry.AEItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ShatterEnchantment extends ConfigurableEnchantment {
    public ShatterEnchantment() {
        super(Rarity.RARE, AEEnchantmentCategory.RANGED, EquipmentSlot.MAINHAND, AEEnchantments.SHATTER_ID);
    }

    @SubscribeEvent
    public static void handleDamage(final LivingAttackEvent event) {
        if (event.getSource().getDirectEntity() instanceof ShardArrow) {
            event.getSource().setMagic();
        }
    }

    @SubscribeEvent // TODO :: check for improvements - what if the arrow gets spawned by sth. else (alternative would be to mixin into the items -> less compatible)
    public static void applyAmmoCost(final EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ShardArrow shardArrow) {
            if (shardArrow.getOwner() instanceof Player player && !player.isCreative()) {
                int slot = player.getInventory().findSlotMatchingItem(Items.AMETHYST_SHARD.getDefaultInstance());

                if (slot != -1) {
                    player.getInventory().getItem(slot).shrink(1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleAmmo(final LivingGetProjectileEvent event) {
        int level = event.getProjectileWeaponItemStack().getEnchantmentLevel(AEEnchantments.SHATTER.get());

        if (level > 0) {
            if (event.getEntity() instanceof Player player) {
                int slot = player.getInventory().findSlotMatchingItem(Items.AMETHYST_SHARD.getDefaultInstance());

                if (slot == -1) {
                    return;
                }

                event.setProjectileItemStack(createAmmo());
            } else {
                event.setProjectileItemStack(createAmmo());
            }
        }
    }

    private static ItemStack createAmmo() {
        ItemStack stack = new ItemStack(AEItems.SHARD_ARROW.get());
        stack.setCount(stack.getMaxStackSize());
        return stack;
    }
}
