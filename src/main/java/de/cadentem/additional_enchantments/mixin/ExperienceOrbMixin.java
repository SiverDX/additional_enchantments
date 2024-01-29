package de.cadentem.additional_enchantments.mixin;

import de.cadentem.additional_enchantments.enchantments.HydroShockEnchantment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.UUID;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @ModifyArg(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static Entity additional_enchantments$makeExperienceFireImmune(final Entity experienceOrb) {
        if (!HydroShockEnchantment.FIRE_IMMUNE_LOOT.isEmpty() && experienceOrb.getLevel() instanceof ServerLevel serverLevel) {
            boolean shouldMakeImmune = false;

            for (String uuid : HydroShockEnchantment.FIRE_IMMUNE_LOOT) {
                Entity entity = serverLevel.getEntity(UUID.fromString(uuid));

                if (entity != null && experienceOrb.distanceTo(entity) <= 64) {
                    shouldMakeImmune = true;
                }
            }

            if (shouldMakeImmune) {
                return new ExperienceOrb(experienceOrb.getLevel(), experienceOrb.getX(), experienceOrb.getY(), experienceOrb.getZ(), ((ExperienceOrb) experienceOrb).value) {
                    @Override
                    public boolean fireImmune() {
                        return true;
                    }
                };
            }
        }

        return experienceOrb;
    }
}
