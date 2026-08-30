package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.attachments.ProjectileHomingData;
import de.cadentem.additional_enchantments.server.conditions.Conditions;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

public record Homing(Identifier id, Optional<HolderSet<EntityType<?>>> projectiles, LootItemCondition targetCondition, HomingRange searchRange, LevelBasedValue velocityMultiplier, LevelBasedValue maxTurnPerTick, int priority) {
    public static final Codec<Homing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(Homing::id),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("projectiles").forGetter(Homing::projectiles), // TODO :: check if it can be limited by item?
            LootItemCondition.DIRECT_CODEC.fieldOf("target_condition").forGetter(Homing::targetCondition),
            HomingRange.CODEC.fieldOf("search_range").forGetter(Homing::searchRange),
            LevelBasedValue.CODEC.fieldOf("velocity_multiplier").forGetter(Homing::velocityMultiplier),
            LevelBasedValue.CODEC.optionalFieldOf("max_turn_per_tick", LevelBasedValue.constant(ProjectileHomingData.NO_MAX_TURN)).forGetter(Homing::maxTurnPerTick),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(Homing::priority)
    ).apply(instance, Homing::new));

    public Mapped map(final int enchantmentLevel) {
        return new Mapped(id, projectiles, targetCondition, searchRange.map(enchantmentLevel), velocityMultiplier.calculate(enchantmentLevel), maxTurnPerTick.calculate(enchantmentLevel), priority);
    }

    public record Mapped(Identifier id, Optional<HolderSet<EntityType<?>>> projectiles, LootItemCondition targetCondition, HomingRange.Mapped searchRange, float velocityMultiplier, float maxTurnPerTick, int priority) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(Mapped::id),
                RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("projectiles").forGetter(Mapped::projectiles),
                LootItemCondition.DIRECT_CODEC.fieldOf("target_condition").forGetter(Mapped::targetCondition),
                HomingRange.Mapped.CODEC.fieldOf("search_range").forGetter(Mapped::searchRange),
                Codec.FLOAT.fieldOf("velocity_multiplier").forGetter(Mapped::velocityMultiplier),
                Codec.FLOAT.fieldOf("max_turn_per_tick").forGetter(Mapped::maxTurnPerTick),
                Codec.INT.fieldOf("priority").forGetter(Mapped::priority)
        ).apply(instance, Mapped::new));

        public boolean isValidForProjectile(final Projectile projectile) {
            return projectiles.map(projectileTypes -> projectileTypes.contains(projectile.getType().builtInRegistryHolder()))
                    .orElse(true);
        }

        public boolean isValidTarget(final ServerLevel serverLevel, final Projectile projectile, final Entity target) {
            return targetCondition.test(Conditions.homingContext(serverLevel, projectile, target));
        }
    }
}
