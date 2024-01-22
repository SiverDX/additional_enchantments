package de.cadentem.additional_enchantments.core.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WitherEffect extends MobEffect {
    public WitherEffect() {
        super(MobEffectCategory.HARMFUL, 3484199);
    }

    @Override
    public void applyEffectTick(final LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(livingEntity.damageSources().wither(), (1 + amplifier) * 0.5f);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % (Math.max(1, 20 - amplifier / 2)) == 0;
    }
}