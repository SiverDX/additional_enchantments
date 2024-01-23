package de.cadentem.additional_enchantments.core.effects;

import de.cadentem.additional_enchantments.data.EntityTags;
import de.cadentem.additional_enchantments.registry.AEMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PlagueEffect extends MobEffect {
    public PlagueEffect() {
        super(MobEffectCategory.HARMFUL, 5149489);
    }

    @Override
    public void applyEffectTick(final LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(DamageSource.MAGIC, (1 + amplifier) * 0.5f);

        if ((1 + amplifier) * 0.075d > livingEntity.getRandom().nextDouble()) {
            List<LivingEntity> entities = livingEntity.getLevel().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(2 * (1 + amplifier)), plagueTarget -> {
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
