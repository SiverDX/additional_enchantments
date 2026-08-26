package de.cadentem.additional_enchantments.enchantments.fluid_vision;

import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.core.HolderSet;
import net.neoforged.neoforge.fluids.FluidType;

// TODO :: or use predicate?
// TODO :: keep in mind to reload client level after changing instances
public record FluidVision(HolderSet<FluidType> fluids) {
}
