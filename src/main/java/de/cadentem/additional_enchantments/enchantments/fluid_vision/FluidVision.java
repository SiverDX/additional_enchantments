package de.cadentem.additional_enchantments.enchantments.fluid_vision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record FluidVision(ResourceLocation id, HolderSet<FluidType> fluidTypes, LevelBasedValue percentage) {
    public static final Codec<FluidVision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(FluidVision::id),
            RegistryCodecs.homogeneousList(NeoForgeRegistries.FLUID_TYPES.key()).fieldOf("fluid_types").forGetter(FluidVision::fluidTypes),
            LevelBasedValue.CODEC.fieldOf("percentage").forGetter(FluidVision::percentage)
    ).apply(instance, FluidVision::new));

    public Mapped map(final int level) {
        return new Mapped(id, fluidTypes, Mth.clamp(percentage.calculate(level), 0, 1));
    }

    public record Mapped(ResourceLocation id, HolderSet<FluidType> fluidTypes, float percentage) {
        public static final Codec<Mapped> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(Mapped::id),
                RegistryCodecs.homogeneousList(NeoForgeRegistries.FLUID_TYPES.key()).fieldOf("fluid_types").forGetter(Mapped::fluidTypes),
                Codec.FLOAT.fieldOf("percentage").forGetter(Mapped::percentage)
        ).apply(instance, Mapped::new));

        public static final Mapped NONE = new Mapped(AE.location("none"), HolderSet.direct(), 1);
    }
}
