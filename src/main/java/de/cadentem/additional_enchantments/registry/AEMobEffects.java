package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.effects.PlagueEffect;
import de.cadentem.additional_enchantments.core.effects.WitherEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AE.MODID);

    public static final RegistryObject<PlagueEffect> PLAGUE = MOB_EFFECTS.register("plague", PlagueEffect::new);
    public static final RegistryObject<WitherEffect> WITHER = MOB_EFFECTS.register("wither", WitherEffect::new);
}
