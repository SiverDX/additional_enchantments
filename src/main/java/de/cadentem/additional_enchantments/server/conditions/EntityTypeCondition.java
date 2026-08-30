package de.cadentem.additional_enchantments.server.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public record EntityTypeCondition(Type type, LootContext.EntityTarget entityTarget) implements LootItemCondition {
    public static final MapCodec<EntityTypeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(EntityTypeCondition::type),
            LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(EntityTypeCondition::entityTarget)
    ).apply(instance, EntityTypeCondition::new));

    @Override
    public boolean test(final LootContext context) {
        Entity entity = context.getOptionalParameter(entityTarget.contextParam());

        if (entity == null) {
            return false;
        }

        return switch (type) {
            case Type.LIVING_ENTITY -> entity instanceof LivingEntity;
            case Type.ENEMY -> entity instanceof Enemy || entity.getType().getCategory() == MobCategory.MONSTER;
            case Type.TAMED -> entity instanceof TamableAnimal tamable && tamable.isTame();
            case Type.ANIMAL -> entity instanceof Animal;
            case Type.ITEM -> entity instanceof ItemEntity;
            case Type.EXPERIENCE_ORB -> entity instanceof ExperienceOrb;
        };
    }

    @Override
    public @NotNull MapCodec<EntityTypeCondition> codec() {
        return CODEC;
    }

    public enum Type implements StringRepresentable {
        LIVING_ENTITY("living_entity"),
        ENEMY("enemy"),
        TAMED("tamed"),
        ANIMAL("animal"),
        ITEM("item"),
        EXPERIENCE_ORB("experience_orb");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
