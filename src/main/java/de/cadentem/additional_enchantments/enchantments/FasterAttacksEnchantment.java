package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class FasterAttacksEnchantment extends ConfigurableEnchantment {
    private static final String ATTRIBUTE_UUID = "578e84b7-327d-4a19-87f1-cb5de98a977d";

    public FasterAttacksEnchantment() {
        super(Rarity.VERY_RARE, AEEnchantmentCategory.MELEE, EquipmentSlot.MAINHAND, AEEnchantments.FASTER_ATTACKS_ID);
    }

    @SubscribeEvent
    public static void handlerPlayerTick(final LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.tickCount % 10 != 0) {
            return;
        }

        AttributeInstance attribute = livingEntity.getAttribute(Attributes.ATTACK_SPEED);

        if (attribute != null) {
            int enchantmentLevel = livingEntity.getMainHandItem().getEnchantmentLevel(AEEnchantments.FASTER_ATTACKS.get());
            AttributeModifier modifier = attribute.getModifier(UUID.fromString(ATTRIBUTE_UUID));

            if (modifier != null) {
                attribute.removeModifier(modifier);
            }

            if (enchantmentLevel > 0) {
                attribute.addTransientModifier(new AttributeModifier(UUID.fromString(ATTRIBUTE_UUID), "Faster Attacks enchantment", enchantmentLevel * ServerConfig.FASTER_ATTACKS_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }
}
