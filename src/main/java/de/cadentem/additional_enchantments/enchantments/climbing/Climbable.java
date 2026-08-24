package de.cadentem.additional_enchantments.enchantments.climbing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record Climbable(ResourceLocation id, BlockPredicate blocks, boolean canStickToWalls, boolean canClimbCeilings) {
    public static final Codec<Climbable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("key").forGetter(Climbable::id),
            BlockPredicate.CODEC.fieldOf("blocks").forGetter(Climbable::blocks),
            Codec.BOOL.optionalFieldOf("can_stick_to_walls", false).forGetter(Climbable::canStickToWalls),
            Codec.BOOL.optionalFieldOf("can_climb_ceilings", false).forGetter(Climbable::canClimbCeilings)
    ).apply(instance, Climbable::new));

    public boolean canClimb(final WorldGenLevel level, final BlockPos position) {
        return blocks.test(level, position);
    }

    public boolean canStickToWalls(final WorldGenLevel level, final BlockPos climbPosition) {
        return blocks.test(level, climbPosition);
    }
}
