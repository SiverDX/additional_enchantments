package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.config.VisionConfig;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomGlowParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    private final List<Integer> colors;
    private final double colorShiftRate;
    private final double colorOffset;

    protected CustomGlowParticle(final ClientLevel level, final double x, final double y, final double z, final SpriteSet sprites, final List<Integer> colors, final double colorShiftRate) {
        super(level, x, y, z, 0.5 - level.getRandom().nextDouble(), 0, 0.5 - level.getRandom().nextDouble());
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 0.75F;
        this.hasPhysics = false;
        this.colors = colors;
        this.colorShiftRate = colorShiftRate;
        this.colorOffset = level.getRandom().nextDouble();
        setSpriteFromAge(sprites);
        updateColor();
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
        updateColor();
    }

    public void updateColor() {
        int color = ColorUtils.lerpColor(colors, colorShiftRate, colorOffset);
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;

        setColor(red, green, blue);
        setAlpha(FastColor.ARGB32.alpha(color) / 255f);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(@NotNull final SimpleParticleType type, @NotNull final ClientLevel level, final double x, final double y, final double z, final double blockId, final double enchantmentLevel, final double ignored) {
            //noinspection DataFlowIssue -> level is present
            IdMap<Holder<Block>> map = Minecraft.getInstance().level.registryAccess().registryOrThrow(ForgeRegistries.BLOCKS.getRegistryKey()).asHolderIdMap();
            Holder<Block> holder = map.byIdOrThrow((int) blockId);

            VisionConfig.VisionData visionData = VisionConfig.get(holder.value(), (int) enchantmentLevel);

            if (visionData == null && holder.is(AEBlockTags.TREASURES)) {
                visionData = VisionConfig.SpecialBlock.TREASURE.get((int) enchantmentLevel);
            }

            if (visionData == null) {
                AE.LOG.warn("Invalid particle data for block [{}] at level [{}]", holder.value(), enchantmentLevel);
                return new CustomGlowParticle(level, x, y, z, sprites, List.of(), 1);
            }

            CustomGlowParticle particle = new CustomGlowParticle(level, x, y, z, sprites, visionData.colorsARGB(), visionData.colorShiftRate());

            particle.yd *= 0.2F;
            particle.xd *= 0.1F;
            particle.zd *= 0.1F;

            particle.setLifetime((int) (8 / (level.getRandom().nextDouble() * 0.8 + 0.2)));
            return particle;
        }
    }
}