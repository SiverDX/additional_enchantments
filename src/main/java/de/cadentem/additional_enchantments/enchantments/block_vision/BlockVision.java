package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.util.ShiftingColor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record BlockVision(ResourceLocation id, Either<SpecialBlockType, HolderSet<Block>> blocks, LevelBasedValue range, DisplayType displayType, int particleRate, ShiftingColor color) {
    public static final Codec<BlockVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(BlockVision::id),
            Codec.either(SpecialBlockType.CODEC, RegistryCodecs.homogeneousList(Registries.BLOCK)).fieldOf("blocks").forGetter(BlockVision::blocks),
            LevelBasedValue.CODEC.fieldOf("range").forGetter(BlockVision::range),
            DisplayType.CODEC.fieldOf("display_type").forGetter(BlockVision::displayType),
            Codec.INT.optionalFieldOf("particle_rate", 0).forGetter(BlockVision::particleRate),
            ShiftingColor.CODEC.fieldOf("color").forGetter(BlockVision::color)
    ).apply(instance, BlockVision::new));

    public Mapped map(final int level) {
        return new Mapped(id, blocks, (int) range.calculate(level), displayType, particleRate, color.map());
    }

    public record Mapped(ResourceLocation id, Either<SpecialBlockType, HolderSet<Block>> blocks, int range, DisplayType displayType, int particleRate, ShiftingColor.Mapped color) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(Mapped::id),
                Codec.either(SpecialBlockType.CODEC, RegistryCodecs.homogeneousList(Registries.BLOCK)).fieldOf("blocks").forGetter(Mapped::blocks),
                Codec.INT.fieldOf("range").forGetter(Mapped::range),
                DisplayType.CODEC.fieldOf("display_type").forGetter(Mapped::displayType),
                Codec.INT.optionalFieldOf("particle_rate", 0).forGetter(Mapped::particleRate),
                ShiftingColor.Mapped.CODEC.fieldOf("color").forGetter(Mapped::color)
        ).apply(instance, Mapped::new));

        /** If the passed state is 'null', it will return the range as well */
        public int getRange(@Nullable final Block block) {
            if (block == null) {
                return range;
            }

            //noinspection deprecation -> ignore
            Holder.Reference<Block> holder = block.builtInRegistryHolder();

            if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
                return range;
            }

            return 0;
        }

        public ShiftingColor.Mapped getMappedColors(final Block block) {
            //noinspection deprecation -> ignore
            Holder.Reference<Block> holder = block.builtInRegistryHolder();

            if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
                return color;
            }

            return ShiftingColor.Mapped.NONE;
        }

        public DisplayType getDisplayType(final Block block) {
            //noinspection deprecation -> ignore
            Holder.Reference<Block> holder = block.builtInRegistryHolder();

            if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
                return displayType;
            }

            return DisplayType.NONE;
        }

        public int getParticleRate(final Block block) {
            //noinspection deprecation -> ignore
            Holder.Reference<Block> holder = block.builtInRegistryHolder();

            if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
                return particleRate;
            }

            return -1;
        }
    }

    public enum SpecialBlockType implements StringRepresentable {
        TREASURES("treasures"),
        FILLED_CONTAINERS("filled_containers"); // TODO :: implement

        public static final Codec<SpecialBlockType> CODEC = StringRepresentable.fromEnum(SpecialBlockType::values);
        private final String name;

        SpecialBlockType(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public enum DisplayType implements StringRepresentable {
        OUTLINE("outline"),
        PARTICLES("particles"),
        SIMPLE_SHADER("simple_shader"),
        NONE("none");

        public static final Codec<DisplayType> CODEC = StringRepresentable.fromEnum(DisplayType::values);
        private final String name;

        DisplayType(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
