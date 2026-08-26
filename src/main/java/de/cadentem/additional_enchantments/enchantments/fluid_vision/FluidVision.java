package de.cadentem.additional_enchantments.enchantments.fluid_vision;

import net.minecraft.core.HolderSet;
import net.neoforged.neoforge.fluids.FluidType;

// TODO :: or use predicate?
// TODO :: keep in mind to reload client level after changing instances
// TODO :: add resource loc to block vision
public record FluidVision(HolderSet<FluidType> fluids) {
}
