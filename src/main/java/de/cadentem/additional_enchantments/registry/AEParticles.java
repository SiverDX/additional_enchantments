package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AEParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registry.PARTICLE_TYPE_REGISTRY, AE.MODID);

    public static final RegistryObject<SimpleParticleType> PLAGUE = PARTICLE_TYPES.register("plague", () -> new SimpleParticleType(false));
}
