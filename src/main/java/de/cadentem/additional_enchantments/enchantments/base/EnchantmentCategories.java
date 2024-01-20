package de.cadentem.additional_enchantments.enchantments.base;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.Tags;

public class EnchantmentCategories {
    public static final EnchantmentCategory RANGED = EnchantmentCategory.create("ranged", item -> {
        if (item instanceof BowItem || item instanceof CrossbowItem) {
            return true;
        }

        return item.builtInRegistryHolder().is(Tags.Items.TOOLS_BOWS) || item.builtInRegistryHolder().is(Tags.Items.TOOLS_CROSSBOWS);
    });

    public static final EnchantmentCategory MELEE = EnchantmentCategory.create("melee", item -> {
        if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) {
            return true;
        }

        return item.builtInRegistryHolder().is(Tags.Items.TOOLS_SWORDS) || item.builtInRegistryHolder().is(Tags.Items.TOOLS_AXES) || item.builtInRegistryHolder().is(Tags.Items.TOOLS_TRIDENTS);
    });
}
