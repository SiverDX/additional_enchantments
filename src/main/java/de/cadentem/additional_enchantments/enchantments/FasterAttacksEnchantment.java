package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.enchantments.config.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.config.EnchantmentCategories;
import de.cadentem.additional_enchantments.registry.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class FasterAttacksEnchantment extends ConfigurableEnchantment {
    private static final String ATTRIBUTE_UUID = "578e84b7-327d-4a19-87f1-cb5de98a977d";

    public FasterAttacksEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategories.MELEE, EquipmentSlot.MAINHAND);
    }

    @SubscribeEvent
    public static void handlerPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.player.tickCount % 10 != 0) {
            return;
        }

        AttributeInstance attribute = event.player.getAttribute(Attributes.ATTACK_SPEED);

        if (attribute != null) {
            int level = event.player.getMainHandItem().getEnchantmentLevel(Enchantments.FASTER_ATTACKS.get());
            AttributeModifier modifier = attribute.getModifier(UUID.fromString(ATTRIBUTE_UUID));

            if (modifier != null) {
                attribute.removeModifier(modifier);
            }

            if (level > 0) {
                attribute.addTransientModifier(new AttributeModifier(UUID.fromString(ATTRIBUTE_UUID), "Faster Attacks enchantment", (double) level * 0.15, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
    }
}
