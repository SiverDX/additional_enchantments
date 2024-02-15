package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AEParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, AE.MODID);

    public static final RegistryObject<SimpleParticleType> PLAGUE = PARTICLE_TYPES.register("plague", () -> new SimpleParticleType(false));
}
