package de.cadentem.additional_enchantments.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class PlagueParticle extends SmokeParticle {
    public PlagueParticle(final ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float quadSizeMultiplier, final SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, quadSizeMultiplier, sprites);
        setColor(0.43f, 0.51f, 0.21f);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(@NotNull final SimpleParticleType type, @NotNull final ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PlagueParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.0F, this.sprites);
        }
    }
}
