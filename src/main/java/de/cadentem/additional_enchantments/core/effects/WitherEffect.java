package de.cadentem.additional_enchantments.core.effects;

import de.cadentem.additional_enchantments.config.ServerConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WitherEffect extends MobEffect {
    public WitherEffect() {
        super(MobEffectCategory.HARMFUL, 3484199);
    }

    @Override
    public void applyEffectTick(final LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(livingEntity.damageSources().wither(), (ServerConfig.WITHER_DAMAGE_BASE.get().floatValue() + amplifier) * ServerConfig.WITHER_DAMAGE_MULTIPLIER.get().floatValue());
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % (Math.max(1, 20 - amplifier / ServerConfig.WITHER_DAMAGE_TICK_RATE.get())) == 0;
    }
}