package de.cadentem.additional_enchantments.enchantments.green_foot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record GreenFootEffect(List<GrowthEntry> entries, LevelBasedValue extraRange) implements EnchantmentEntityEffect {
    public static final MapCodec<GreenFootEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GrowthEntry.CODEC.listOf().fieldOf("growth_entries").forGetter(GreenFootEffect::entries),
            LevelBasedValue.CODEC.fieldOf("extra_range").forGetter(GreenFootEffect::extraRange)
    ).apply(instance, GreenFootEffect::new));

    @Override
    public void apply(@NotNull final ServerLevel level, final int enchantmentLevel, @NotNull final EnchantedItemInUse item, @NotNull final Entity entity, @NotNull final Vec3 origin) {
        if (!entity.onGround()) {
            return;
        }

        BlockPos originPosition = BlockPos.containing(origin);
        apply(level, originPosition, entity.getRandom(), item, enchantmentLevel);

        int extraRange = (int) Math.ceil(this.extraRange.calculate(enchantmentLevel));

        if (extraRange == 0) {
            return;
        }

        BlockPos searchPosition = originPosition.mutable();

        for (int x = -extraRange; x <= extraRange; x++) {
            for (int z = -extraRange; z <= extraRange; z++) {
                if (item.itemStack().isEmpty()) {
                    // Item broke
                    return;
                }

                apply(level, searchPosition.offset(x, 0, z), entity.getRandom(), item, enchantmentLevel);
            }
        }
    }

    private void apply(final ServerLevel level, final BlockPos position, final RandomSource random, final EnchantedItemInUse item, final int enchantmentLevel) {
        Set<GrowthEntry> entries = new HashSet<>();

        for (GrowthEntry entry : this.entries) {
            if (entry.canApply(level, position)) {
                entries.add(entry);
            }
        }

        if (entries.isEmpty()) {
            return;
        }

        GrowthEntry growth = null;

        for (GrowthEntry entry : entries) {
            if (growth == null || growth.priority() < entry.priority()) {
                growth = entry;
            }
        }

        growth.apply(level, position, random, item, enchantmentLevel);
    }

    public record GrowthEntry(BlockPredicate predicate, LevelBasedValue probability, int priority, int damage) {
        public static final Codec<GrowthEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.fieldOf("predicate").forGetter(GrowthEntry::predicate),
                LevelBasedValue.CODEC.fieldOf("probability").forGetter(GrowthEntry::probability),
                Codec.INT.fieldOf("priority").forGetter(GrowthEntry::priority),
                Codec.INT.fieldOf("damage").forGetter(GrowthEntry::damage)
        ).apply(instance, GrowthEntry::new));

        public void apply(final ServerLevel level, final BlockPos position, final RandomSource random, final EnchantedItemInUse item, final int enchantmentLevel) {
            if (item.itemStack().isEmpty() || random.nextDouble() > probability.calculate(enchantmentLevel)) {
                return;
            }

            BlockState state = level.getBlockState(position);

            if (state.getBlock() instanceof BonemealableBlock block && block.isValidBonemealTarget(level, position, state)) {
                block.performBonemeal(level, random, position, state);

                // '15' is the particle count, see BoneMealItem#addGrowthParticles
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, position, 15);
            }

            damageItem(item, damage);
        }

        public boolean canApply(final ServerLevel level, final BlockPos position) {
            return predicate.test(level, position);
        }

        private void damageItem(final EnchantedItemInUse item, int amount) {
            if (amount == 0 || item.owner() == null || item.inSlot() == null) {
                return;
            }

            item.itemStack().hurtAndBreak(amount, item.owner(), item.inSlot());
        }
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
