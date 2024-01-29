package de.cadentem.additional_enchantments.registry;

import com.mojang.serialization.Codec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.loot_modifiers.VoidingModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AELootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AE.MODID);

    public static final RegistryObject<Codec<VoidingModifier>> ADDITIONAL_ORE_LOOT = LOOT_MODIFIERS.register(VoidingModifier.ID, () -> VoidingModifier.CODEC);
}
