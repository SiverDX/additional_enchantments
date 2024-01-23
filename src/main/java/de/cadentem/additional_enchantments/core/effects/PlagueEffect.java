package de.cadentem.additional_enchantments.core.effects;

import com.mojang.math.Vector3f;
import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.registry.AEMobEffects;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlagueEffect extends MobEffect {
    private static final DustParticleOptions PLAGUE_OPTION = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(5635925)), 3);

    public PlagueEffect() {
        super(MobEffectCategory.HARMFUL, 5149489);
    }

    @Override
    public void applyEffectTick(final LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(DamageSource.MAGIC, (1 + amplifier) * 0.5f);

        if ((1 + amplifier) * 0.05d > livingEntity.getRandom().nextDouble()) {
            AABB boundingBox = livingEntity.getBoundingBox().inflate(1 + amplifier);

            if (livingEntity.getLevel() instanceof ServerLevel serverLevel) {
                Vec3 center = boundingBox.getCenter();
                double size = boundingBox.getSize();

                serverLevel.sendParticles(PLAGUE_OPTION, center.x(), center.y(), center.z(), (int) (16 * Math.pow((1 + amplifier), 1.5)), size / 2, size / 2, size / 2, 0);
            }

            List<LivingEntity> entities = livingEntity.getLevel().getEntitiesOfClass(LivingEntity.class, boundingBox, plagueTarget -> {
                if (plagueTarget instanceof Player || plagueTarget instanceof TamableAnimal tamable && tamable.getOwner() instanceof Player) {
                    return false;
                }

                if (plagueTarget.getType().is(EntityTags.PLAGUE_BLACKLIST)) {
                    return false;
                }

                return !plagueTarget.hasEffect(AEMobEffects.PLAGUE.get());
            });

            entities.forEach(plagueTarget -> {
                if (plagueTarget.getRandom().nextBoolean()) {
                    plagueTarget.addEffect(new MobEffectInstance(AEMobEffects.PLAGUE.get(), 20 * (3 + amplifier / 2), amplifier / 2));
                }
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % (Math.max(1, 20 - (1 + amplifier) / 3)) == 0;
    }
}
