package de.cadentem.additional_enchantments.enchantments;

import com.mojang.serialization.MapCodec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.block_vision.BlockVisionEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class AEEnchantmentRegistry {
    public static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> LOCATION_EFFECT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, AE.MODID);
    public static final DeferredHolder<MapCodec<? extends EnchantmentLocationBasedEffect>, MapCodec<BlockVisionEffect>> BLOCK_VISION_EFFECT = LOCATION_EFFECT_REGISTRY.register("block_vision", () -> BlockVisionEffect.CODEC);

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, AE.MODID);
    /** It's either a custom component or using the 'TICK' component and custom logic to not always run each tick */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockVisionEffect>> BLOCK_VISION_COMPONENT = register("block_vision", builder -> builder.persistent(BlockVisionEffect.CODEC.codec()));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(final String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENT_REGISTRY.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
