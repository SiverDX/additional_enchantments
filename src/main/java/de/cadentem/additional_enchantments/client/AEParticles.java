package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.common.particles.CustomGlowParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(Dist.CLIENT)
public class AEParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, AE.MODID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOW = REGISTRY.register("glow", () -> new SimpleParticleType(false));

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AEParticles.GLOW.value(), CustomGlowParticle.Provider::new);
    }
}
