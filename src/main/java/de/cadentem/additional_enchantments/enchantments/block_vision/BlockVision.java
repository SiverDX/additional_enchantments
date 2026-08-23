package de.cadentem.additional_enchantments.enchantments.block_vision;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.data.AEBlockTags;
import de.cadentem.additional_enchantments.util.Colors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public record BlockVision(Either<SpecialBlockType, HolderSet<Block>> blocks, DisplayType displayType, int range, List<ColorEntry> colors, int particleRate, double colorShiftRate) {
    public static final Codec<BlockVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(SpecialBlockType.CODEC, RegistryCodecs.homogeneousList(Registries.BLOCK)).fieldOf("blocks").forGetter(BlockVision::blocks),
            DisplayType.CODEC.fieldOf("display_type").forGetter(BlockVision::displayType),
            Codec.INT.fieldOf("range").forGetter(BlockVision::range),
            ColorEntry.CODEC.listOf().fieldOf("colors").forGetter(BlockVision::colors),
            Codec.INT.optionalFieldOf("particle_rate", 10).forGetter(BlockVision::particleRate),
            Codec.DOUBLE.optionalFieldOf("color_shift_rate", 1.0).forGetter(BlockVision::colorShiftRate)
    ).apply(instance, BlockVision::new));

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

    public List<Integer> getColors(final Block block) {
        //noinspection deprecation -> ignore
        Holder.Reference<Block> holder = block.builtInRegistryHolder();

        if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
            return colors.stream().map(color -> Colors.withAlpha(color.color().getValue(), color.alpha())).toList();
        }

        return List.of();
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

    public double getColorShiftRate(final Block block) {
        //noinspection deprecation -> ignore
        Holder.Reference<Block> holder = block.builtInRegistryHolder();

        if (blocks.map(data -> holder.is(AEBlockTags.TREASURES), data -> data.contains(holder))) {
            return colorShiftRate;
        }

        return -1;
    }

    public record ColorEntry(TextColor color, float alpha) {
        public static final Codec<ColorEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TextColor.CODEC.fieldOf("color").forGetter(ColorEntry::color),
                Codec.FLOAT.optionalFieldOf("alpha", 0.3f).forGetter(ColorEntry::alpha)
        ).apply(instance, ColorEntry::new));
    }

    public enum SpecialBlockType implements StringRepresentable {
        TREASURES("treasures"),
        FILLED_CONTAINERS("filled_containers");

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
