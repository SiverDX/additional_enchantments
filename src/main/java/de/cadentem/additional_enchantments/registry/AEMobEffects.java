package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.effects.PoisonEffect;
import de.cadentem.additional_enchantments.core.effects.WitherEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AE.MODID);

    public static final RegistryObject<PoisonEffect> POISON = MOB_EFFECTS.register("poison", PoisonEffect::new);
    public static final RegistryObject<WitherEffect> WITHER = MOB_EFFECTS.register("wither", WitherEffect::new);
}
