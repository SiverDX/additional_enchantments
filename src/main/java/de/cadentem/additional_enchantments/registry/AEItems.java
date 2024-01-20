package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.item.ShardArrowItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AE.MODID);

    public static final RegistryObject<ShardArrowItem> SHARD_ARROW = ITEMS.register("shard_arrow", () -> new ShardArrowItem(new Item.Properties()));
}
