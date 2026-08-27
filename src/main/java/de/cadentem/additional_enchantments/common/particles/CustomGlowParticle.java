package de.cadentem.additional_enchantments.common.particles;

import de.cadentem.additional_enchantments.attachments.AEDataAttachments;
import de.cadentem.additional_enchantments.attachments.TreasureFinderData;
import de.cadentem.additional_enchantments.util.Colors;
import de.cadentem.additional_enchantments.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Basically just a copy of {@link GlowSquidProvider} but allows setting a custom color */
public class CustomGlowParticle extends GlowParticle {
    /** Allows each particle to start at a separate section of the (potential) color range */
    private final double colorOffset;
    private List<Integer> colors;

    protected CustomGlowParticle(final ClientLevel level, final double x, final double y, final double z, final double blockId, final double ySpeed, final double colorOffset, final SpriteSet sprites) {
        super(level, x, y, z, 0.5 - level.getRandom().nextDouble(), ySpeed, 0.5 - level.getRandom().nextDouble(), sprites);
        this.colorOffset = colorOffset;
    }

    @Override
    public void tick() {
        super.tick();

        if (colors != null) {
            int argb = Colors.withAlpha(Functions.lerpColor(colors, 1, colorOffset), 1);

            float red = FastColor.ARGB32.red(argb) / 255f;
            float green = FastColor.ARGB32.green(argb) / 255f;
            float blue = FastColor.ARGB32.blue(argb) / 255f;
            setColor(red, green, blue);
        }
    }

    public void setColors(final List<Integer> colors) {
        this.colors = colors;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(@NotNull final SimpleParticleType type, @NotNull final ClientLevel level, final double x, final double y, final double z, final double blockId, final double ySpeed, final double colorOffset) {
            CustomGlowParticle particle = new CustomGlowParticle(level, x, y, z, blockId, ySpeed, colorOffset, sprites);

            LocalPlayer player = Minecraft.getInstance().player;
            List<Integer> colors = List.of();

            //noinspection DataFlowIssue -> player is present
            TreasureFinderData vision = player.getExistingData(AEDataAttachments.TREASURE_FINDER).orElse(null);

            if (vision != null) {
                colors = vision.getColors(BuiltInRegistries.BLOCK.byId((int) blockId));
            }

            int argb = Functions.lerpColor(colors);
            float red = FastColor.ARGB32.red(argb) / 255f;
            float green = FastColor.ARGB32.green(argb) / 255f;
            float blue = FastColor.ARGB32.blue(argb) / 255f;
            particle.setColor(red, green, blue);

            particle.yd *= 0.2F;
            particle.xd *= 0.1F;
            particle.zd *= 0.1F;

            particle.setColors(colors);
            particle.setLifetime((int) (8 / (level.getRandom().nextDouble() * 0.8 + 0.2)));
            return particle;
        }
    }
}
