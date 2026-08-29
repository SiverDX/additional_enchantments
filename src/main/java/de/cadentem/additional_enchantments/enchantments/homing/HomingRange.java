package de.cadentem.additional_enchantments.enchantments.homing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record HomingRange(LevelBasedValue front, LevelBasedValue back, LevelBasedValue left, LevelBasedValue right, LevelBasedValue up, LevelBasedValue down) {
    public static final LevelBasedValue NONE = LevelBasedValue.constant(0);

    public static final Codec<HomingRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("front", NONE).forGetter(HomingRange::front),
            LevelBasedValue.CODEC.optionalFieldOf("back", NONE).forGetter(HomingRange::back),
            LevelBasedValue.CODEC.optionalFieldOf("left", NONE).forGetter(HomingRange::left),
            LevelBasedValue.CODEC.optionalFieldOf("right", NONE).forGetter(HomingRange::right),
            LevelBasedValue.CODEC.optionalFieldOf("up", NONE).forGetter(HomingRange::up),
            LevelBasedValue.CODEC.optionalFieldOf("down", NONE).forGetter(HomingRange::down)
    ).apply(instance, HomingRange::new));

    public static Builder builder() {
        return new Builder();
    }

    public Mapped map(final int enchantmentLevel) {
        return new Mapped(
                front.calculate(enchantmentLevel),
                back.calculate(enchantmentLevel),
                left.calculate(enchantmentLevel),
                right.calculate(enchantmentLevel),
                up.calculate(enchantmentLevel),
                down.calculate(enchantmentLevel)
        );
    }

    public static class Builder {
        private LevelBasedValue front = NONE;
        private LevelBasedValue back = NONE;
        private LevelBasedValue left = NONE;
        private LevelBasedValue right = NONE;
        private LevelBasedValue up = NONE;
        private LevelBasedValue down = NONE;

        public Builder front(final LevelBasedValue front) {
            this.front = front;
            return this;
        }

        public Builder back(final LevelBasedValue back) {
            this.back = back;
            return this;
        }

        public Builder left(final LevelBasedValue left) {
            this.left = left;
            return this;
        }

        public Builder right(final LevelBasedValue right) {
            this.right = right;
            return this;
        }

        /** Sets the same value for {@link Builder#left} and {@link Builder#right} */
        public Builder sides(final LevelBasedValue sides) {
            return left(sides).right(sides);
        }

        public Builder up(final LevelBasedValue up) {
            this.up = up;
            return this;
        }

        public Builder down(final LevelBasedValue down) {
            this.down = down;
            return this;
        }

        /** Sets the same value for {@link Builder#up} and {@link Builder#down} */
        public Builder vertical(final LevelBasedValue vertical) {
            return up(vertical).down(vertical);
        }

        /** Sets the same value for all directions */
        public Builder all(final LevelBasedValue value) {
            return front(value).back(value).sides(value).vertical(value);
        }

        public HomingRange build() {
            return new HomingRange(front, back, left, right, up, down);
        }
    }

    /** Range for a specific enchantment level */
    public record Mapped(float front, float back, float left, float right, float up, float down) {
        public static final Mapped NONE = new Mapped(0, 0, 0, 0, 0, 0);

        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("front").forGetter(Mapped::front),
                Codec.FLOAT.fieldOf("back").forGetter(Mapped::back),
                Codec.FLOAT.fieldOf("left").forGetter(Mapped::left),
                Codec.FLOAT.fieldOf("right").forGetter(Mapped::right),
                Codec.FLOAT.fieldOf("up").forGetter(Mapped::up),
                Codec.FLOAT.fieldOf("down").forGetter(Mapped::down)
        ).apply(instance, Mapped::new));

        /** @return The largest value per direction of both ranges */
        public Mapped max(final Mapped other) {
            return new Mapped(
                    Math.max(front, other.front),
                    Math.max(back, other.back),
                    Math.max(left, other.left),
                    Math.max(right, other.right),
                    Math.max(up, other.up),
                    Math.max(down, other.down)
            );
        }
    }
}
