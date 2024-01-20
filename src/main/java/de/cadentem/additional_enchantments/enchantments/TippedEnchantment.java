package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import de.cadentem.additional_enchantments.core.ArrowAccess;
import de.cadentem.additional_enchantments.data.EffectTags;
import de.cadentem.additional_enchantments.enchantments.config.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.config.EnchantmentCategories;
import de.cadentem.additional_enchantments.registry.Enchantments;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class TippedEnchantment extends ConfigurableEnchantment {
    public TippedEnchantment() {
        super(Rarity.RARE, EnchantmentCategories.RANGED, EquipmentSlot.MAINHAND);
    }

    @SubscribeEvent
    public static void markProjectile(final EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Arrow arrow && arrow.getOwner() instanceof LivingEntity livingOwner) {
            if (!((ArrowAccess) arrow).additional_enchantments$hasEffect()) {
                int level = livingOwner.getMainHandItem().getEnchantmentLevel(Enchantments.TIPPED.get());

                if (level > 0) {
                    CapabilityProvider.getCapability(livingOwner).ifPresent(configuration -> {
                        ITag<MobEffect> blacklist = ForgeRegistries.MOB_EFFECTS.tags().getTag(EffectTags.TIPPED_BLACKLIST);
                        List<MobEffect> effects = ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(effect -> !blacklist.contains(effect) && isValidEffect(configuration.effectFilter, effect)).collect(Collectors.toList());

                        List<MobEffect> appliedEffects;

                        if (effects.size() <= level) {
                            appliedEffects = new ArrayList<>(effects);
                        } else {
                            appliedEffects = new ArrayList<>();
                            int count = 0;

                            while (count < level) {
                                MobEffect effect = effects.get(livingOwner.getRandom().nextInt(effects.size()));

                                effects.remove(effect);
                                appliedEffects.add(effect);

                                count++;
                            }
                        }

                        for (MobEffect effect : appliedEffects) {
                            arrow.addEffect(new MobEffectInstance(effect, 20 * (3 + (level * 2)), level - 1));
                        }

                        ((ArrowAccess) arrow).additional_enchantments$setHasModifiedEffect(true);
                    });
                }
            }
        }
    }

    private static boolean isValidEffect(final MobEffectCategory category, final MobEffect effect) {
        if (category == effect.getCategory()) {
            return true;
        }

        return category == MobEffectCategory.NEUTRAL;
    }
}