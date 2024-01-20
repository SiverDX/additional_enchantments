package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Enchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AE.MODID);

    public static final RegistryObject<HomingEnchantment> HOMING = ENCHANTMENTS.register("homing", HomingEnchantment::new);
    public static final RegistryObject<TippedEnchantment> TIPPED = ENCHANTMENTS.register("tipped", TippedEnchantment::new);
    public static final RegistryObject<StraightShotEnchantment> STRAIGHT_SHOT = ENCHANTMENTS.register("straight_shot", StraightShotEnchantment::new);
    public static final RegistryObject<FasterAttacksEnchantment> FASTER_ATTACKS = ENCHANTMENTS.register("faster_attacks", FasterAttacksEnchantment::new);
    public static final RegistryObject<ExplosiveTipEnchantment> EXPLOSIVE_TIP = ENCHANTMENTS.register("explosive_tip", ExplosiveTipEnchantment::new);
}
