package de.cadentem.additional_enchantments.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CustomGlowParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected CustomGlowParticle(final ClientLevel level, final double x, final double y, final double z, final double color, final double enchantmentLevel, final double ignored, final SpriteSet sprites) {
        super(level, x, y, z, 0.5 - level.getRandom().nextDouble(), 0, 0.5 - level.getRandom().nextDouble());
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 0.75F;
        this.hasPhysics = false;

        setSpriteFromAge(sprites);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(final float partialTick) {
        float progress = (age + partialTick) / lifetime;
        progress = Mth.clamp(progress, 0, 1);
        int packedLight = super.getLightColor(partialTick);
        int light = packedLight & 255;
        int k = packedLight >> 16 & 255;

        light += (int) (progress * 15 * 16);

        if (light > 240) {
            light = 240;
        }

        return light | k << 16;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(@NotNull final SimpleParticleType type, @NotNull final ClientLevel level, final double x, final double y, final double z, final double color, final double enchantmentLevel, final double ignored) {
            CustomGlowParticle particle = new CustomGlowParticle(level, x, y, z, color, enchantmentLevel, ignored, sprites);

            float red = FastColor.ARGB32.red((int) color) / 255f;
            float green = FastColor.ARGB32.green((int) color) / 255f;
            float blue = FastColor.ARGB32.blue((int) color) / 255f;
            particle.setColor(red, green, blue);

            particle.yd *= 0.2F;
            particle.xd *= 0.1F;
            particle.zd *= 0.1F;

            particle.setLifetime((int) (8 / (level.getRandom().nextDouble() * 0.8 + 0.2)));
            return particle;
        }
    }
}