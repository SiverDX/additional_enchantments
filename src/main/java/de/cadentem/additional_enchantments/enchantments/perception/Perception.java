package de.cadentem.additional_enchantments.enchantments.perception;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.server.conditions.Conditions;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record Perception(ResourceLocation id, LootItemCondition condition, LevelBasedValue range, ShiftingColor color) {
    public static final Codec<Perception> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("key").forGetter(Perception::id),
            LootItemCondition.DIRECT_CODEC.fieldOf("condition").forGetter(Perception::condition),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(Perception::range),
            ShiftingColor.CODEC.fieldOf("color").forGetter(Perception::color)
    ).apply(instance, Perception::new));

    public Perception.Mapped map(final int level) {
        return new Perception.Mapped(id, condition, (int) range.calculate(level), color.map());
    }

    public record Mapped(ResourceLocation id, LootItemCondition condition, int range, ShiftingColor.Mapped color) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("key").forGetter(Mapped::id),
                LootItemCondition.DIRECT_CODEC.fieldOf("condition").forGetter(Mapped::condition),
                Codec.INT.fieldOf("range").forGetter(Mapped::range),
                ShiftingColor.Mapped.CODEC.fieldOf("color").forGetter(Mapped::color)
        ).apply(instance, Mapped::new));

        public ShiftingColor.Mapped getColor(final ServerLevel level, final LivingEntity perceptionHolder, final Entity target) {
            if (perceptionHolder.distanceTo(target) > range) {
                return ShiftingColor.Mapped.NONE;
            }

            LootContext context = Conditions.createContext(level, target);

            if (condition.test(context)) {
                return color;
            }

            return ShiftingColor.Mapped.NONE;
        }
    }
}
