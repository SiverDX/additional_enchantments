package de.cadentem.additional_enchantments.enchantments;

import com.mojang.serialization.MapCodec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVisionEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

public class AEEnchantmentRegistry {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, AE.MODID);
    public static final DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>, MapCodec<BlockVisionEffect>> BLOCK_VISION_EFFECT = ENTITY_EFFECT_REGISTRY.register("block_vision", () -> BlockVisionEffect.CODEC);

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, AE.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentEntityEffect>>> EQUIPMENT_CHANGE_TRIGGER = register("equipment_change_trigger", builder -> builder.persistent(EnchantmentEntityEffect.CODEC.listOf()));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(final String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENT_REGISTRY.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
