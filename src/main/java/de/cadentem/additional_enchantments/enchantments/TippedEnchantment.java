package de.cadentem.additional_enchantments.enchantments;

import com.google.common.collect.Sets;
import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.data.AEEffectTags;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class TippedEnchantment extends ConfigurableEnchantment {
    public TippedEnchantment() {
        super(Rarity.RARE, AEEnchantmentCategory.RANGED_AND_TRIDENT, EquipmentSlot.MAINHAND, AEEnchantments.TIPPED_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        return other != Enchantments.RIPTIDE && super.checkCompatibility(other);
    }

    @SubscribeEvent
    public static void applyEffectsToTarget(final LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
            ProjectileDataProvider.getCapability(projectile).ifPresent(data -> {
                if (data.hasAddedEffects()) {
                    for (MobEffectInstance effect : data.addedEffects) {
                        event.getEntity().addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier()));
                    }
                }
            });
        }
    }

    public static void applyEffects(final Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingOwner) {
            int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.TIPPED.get());

            if (level > 0) {
                ProjectileDataProvider.getCapability(projectile).ifPresent(projectileData -> {
                    if (projectileData.hasAddedEffects()) {
                        return;
                    }

                    projectileData.addedEffects = Sets.newHashSet();
                    projectileData.tippedEnchantmentLevel = level;

                    PlayerDataProvider.getCapability(livingOwner).ifPresent(playerData -> {
                        ITagManager<MobEffect> tags = ForgeRegistries.MOB_EFFECTS.tags();

                        if (tags != null) {
                            ITag<MobEffect> blacklist = tags.getTag(AEEffectTags.TIPPED_BLACKLIST);
                            List<MobEffect> effects = ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(effect -> !blacklist.contains(effect) && isValidEffect(playerData.effectFilter, effect)).collect(Collectors.toList());

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
                                projectileData.addedEffects.add(new MobEffectInstance(effect, effect.isInstantenous() ? 1 : (int) (20 * (ServerConfig.TIPPED_DURATION_BASE.get() + (level * ServerConfig.TIPPED_DURATION_MULTIPLIER.get()))), level - 1));
                            }
                        }
                    });
                });
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