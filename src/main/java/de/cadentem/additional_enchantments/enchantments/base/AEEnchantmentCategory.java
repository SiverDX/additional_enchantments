package de.cadentem.additional_enchantments.enchantments.base;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.Tags;

@SuppressWarnings("deprecation")
public class AEEnchantmentCategory {
    public static final EnchantmentCategory RANGED = EnchantmentCategory.create("ranged", AEEnchantmentCategory::isRanged);

    public static final EnchantmentCategory MELEE = EnchantmentCategory.create("melee", item -> {
        if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) {
            return true;
        }

        return item.builtInRegistryHolder().is(ItemTags.SWORDS) || item.builtInRegistryHolder().is(ItemTags.AXES) || item.builtInRegistryHolder().is(Tags.Items.TOOLS_TRIDENTS);
    });

    public static final EnchantmentCategory RANGED_AND_TRIDENT = EnchantmentCategory.create("ranged_and_trident", item -> {
        if (isRanged(item)) {
            return true;
        }

        if (item instanceof TridentItem) {
            return true;
        }

        return item.builtInRegistryHolder().is(Tags.Items.TOOLS_TRIDENTS);
    });

    private static boolean isRanged(final Item item) {
        if (item instanceof BowItem || item instanceof CrossbowItem) {
            return true;
        }

        return item.builtInRegistryHolder().is(Tags.Items.TOOLS_BOWS) || item.builtInRegistryHolder().is(Tags.Items.TOOLS_CROSSBOWS);
    }
}
