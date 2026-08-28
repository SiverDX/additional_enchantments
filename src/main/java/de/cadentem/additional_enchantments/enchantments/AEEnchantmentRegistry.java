package de.cadentem.additional_enchantments.enchantments;

import com.mojang.serialization.MapCodec;
import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.climbing.ClimbableEffect;
import de.cadentem.additional_enchantments.enchantments.fluid_vision.FluidVisionEffect;
import de.cadentem.additional_enchantments.enchantments.green_foot.GreenFootEffect;
import de.cadentem.additional_enchantments.enchantments.perception.PerceptionEffect;
import de.cadentem.additional_enchantments.enchantments.treasure_finder.TreasureFinderEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

@EventBusSubscriber
public class AEEnchantmentRegistry {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, AE.MODID);
    public static final DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> LOCATION_EFFECT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, AE.MODID);

    static {
        ENTITY_EFFECT_REGISTRY.register("treasure_finder", () -> TreasureFinderEffect.CODEC);
        ENTITY_EFFECT_REGISTRY.register("climbable", () -> ClimbableEffect.CODEC);
        ENTITY_EFFECT_REGISTRY.register("perception", () -> PerceptionEffect.CODEC);
        ENTITY_EFFECT_REGISTRY.register("fluid_vision", () -> FluidVisionEffect.CODEC);
        ENTITY_EFFECT_REGISTRY.register("green_foot", () -> GreenFootEffect.CODEC);

        LOCATION_EFFECT_REGISTRY.register("treasure_finder", () -> TreasureFinderEffect.CODEC);
        LOCATION_EFFECT_REGISTRY.register("climbable", () -> ClimbableEffect.CODEC);
        LOCATION_EFFECT_REGISTRY.register("perception", () -> PerceptionEffect.CODEC);
        LOCATION_EFFECT_REGISTRY.register("fluid_vision", () -> FluidVisionEffect.CODEC);
        LOCATION_EFFECT_REGISTRY.register("green_foot", () -> GreenFootEffect.CODEC);
    }

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, AE.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentEntityEffect>>> EQUIPMENT_CHANGE_TRIGGER = register("equipment_change_trigger", builder -> builder.persistent(EnchantmentEntityEffect.CODEC.listOf()));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(final String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENT_REGISTRY.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleEquipmentChangeTrigger(final LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        EnchantedItemInUse oldItem = new EnchantedItemInUse(event.getFrom(), event.getSlot(), event.getEntity());

        EnchantmentHelper.runIterationOnItem(event.getFrom(), (enchantment, enchantmentLevel) -> {
            if (!enchantment.value().matchingSlot(event.getSlot())) {
                return;
            }

            enchantment.value().getEffects(AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value()).forEach(effect -> {
                effect.onDeactivated(oldItem, event.getEntity(), event.getEntity().position(), enchantmentLevel);
            });
        });

        EnchantedItemInUse newItem = new EnchantedItemInUse(event.getTo(), event.getSlot(), event.getEntity());

        EnchantmentHelper.runIterationOnItem(event.getTo(), (enchantment, enchantmentLevel) -> {
            if (!enchantment.value().matchingSlot(event.getSlot())) {
                return;
            }

            enchantment.value().getEffects(AEEnchantmentRegistry.EQUIPMENT_CHANGE_TRIGGER.value()).forEach(effect -> {
                effect.apply(player.serverLevel(), enchantmentLevel, newItem, event.getEntity(), event.getEntity().position());
            });
        });
    }
}
