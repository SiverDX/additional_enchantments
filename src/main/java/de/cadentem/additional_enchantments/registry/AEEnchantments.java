package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AE.MODID);

    public static final String HOMING_ID = "homing";
    public static final String TIPPED_ID = "tipped";
    public static final String STRAIGHT_SHOT_ID = "straight_shot";
    public static final String FASTER_ATTACKS_ID = "faster_attacks";
    public static final String EXPLOSIVE_TIP_ID = "explosive_tip";
    public static final String SHATTER_ID = "shatter";
    public static final String PLAGUE_ID = "plague";
    public static final String WITHER_ID = "wither";
    public static final String PERCEPTION_ID = "perception";
    public static final String CONFUSION_ID = "confusion";

    public static final RegistryObject<HomingEnchantment> HOMING = ENCHANTMENTS.register(HOMING_ID, HomingEnchantment::new);
    public static final RegistryObject<TippedEnchantment> TIPPED = ENCHANTMENTS.register(TIPPED_ID, TippedEnchantment::new);
    public static final RegistryObject<StraightShotEnchantment> STRAIGHT_SHOT = ENCHANTMENTS.register(STRAIGHT_SHOT_ID, StraightShotEnchantment::new);
    public static final RegistryObject<FasterAttacksEnchantment> FASTER_ATTACKS = ENCHANTMENTS.register(FASTER_ATTACKS_ID, FasterAttacksEnchantment::new);
    public static final RegistryObject<ExplosiveTipEnchantment> EXPLOSIVE_TIP = ENCHANTMENTS.register(EXPLOSIVE_TIP_ID, ExplosiveTipEnchantment::new);
    public static final RegistryObject<ShatterEnchantment> SHATTER = ENCHANTMENTS.register(SHATTER_ID, ShatterEnchantment::new);
    public static final RegistryObject<PlagueEnchantment> PLAGUE = ENCHANTMENTS.register(PLAGUE_ID, PlagueEnchantment::new);
    public static final RegistryObject<WitherEnchantment> WITHER = ENCHANTMENTS.register(WITHER_ID, WitherEnchantment::new);
    public static final RegistryObject<PerceptionEnchantment> PERCEPTION = ENCHANTMENTS.register(PERCEPTION_ID, PerceptionEnchantment::new);
    public static final RegistryObject<ConfusionEnchantment> CONFUSION = ENCHANTMENTS.register(CONFUSION_ID, ConfusionEnchantment::new);
}
