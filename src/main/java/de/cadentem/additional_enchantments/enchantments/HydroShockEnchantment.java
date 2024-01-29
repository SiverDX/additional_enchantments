package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.mixin.TridentAccess;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class HydroShockEnchantment extends ConfigurableEnchantment {
    public static final List<String> FIRE_IMMUNE_LOOT = new CopyOnWriteArrayList<>();
    private static final String LIGHTNING_BOLT_TAG = AE.MODID + ".";

    public HydroShockEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.TRIDENT, EquipmentSlot.MAINHAND, AEEnchantments.HYDRO_SHOCK_ID);
    }

    @SubscribeEvent(/* In case other things the entity on fire */ priority = EventPriority.LOW)
    public static void handleDamage(final LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target.getLevel().isClientSide()) {
            return;
        }

        Entity attacker = event.getSource().getEntity();
        boolean wasThrown = false;

        if (attacker instanceof LivingEntity livingAttacker) {
            int enchantmentLevel;

            if (event.getSource().getDirectEntity() instanceof TridentAccess trident) {
                enchantmentLevel = trident.getTridentItem().getEnchantmentLevel(AEEnchantments.HYDRO_SHOCK.get());
                wasThrown = true;
            } else {
                enchantmentLevel = livingAttacker.getMainHandItem().getEnchantmentLevel(AEEnchantments.HYDRO_SHOCK.get());
            }

            if (enchantmentLevel > 0) {
                if (target.isSensitiveToWater() || target.isOnFire() || target.isInWater()) {
                    boolean isRainingAt = target.getLevel().isRainingAt(target.blockPosition());
                    float multiplier = (isRainingAt ? 1.5f : 1f) + (enchantmentLevel / 7f);
                    event.setAmount(event.getAmount() * multiplier);
                }

                if (wasThrown && (enchantmentLevel / 5d) > livingAttacker.getRandom().nextDouble()) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(attacker.getLevel());
                    FIRE_IMMUNE_LOOT.add(attacker.getStringUUID());

                    if (bolt != null) {
                        bolt.addTag(LIGHTNING_BOLT_TAG + "safe");
                        bolt.addTag(LIGHTNING_BOLT_TAG + attacker.getStringUUID());
                        bolt.setPos(target.position());
                        attacker.getLevel().addFreshEntity(bolt);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void clearFireImmuneList(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !FIRE_IMMUNE_LOOT.isEmpty()) {
            FIRE_IMMUNE_LOOT.clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void makeLootFireImmune(final LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof LightningBolt bolt && bolt.getTags().contains(LIGHTNING_BOLT_TAG + ".safe") || attacker instanceof LivingEntity && FIRE_IMMUNE_LOOT.contains(attacker.getStringUUID())) {
            Set<ItemEntity> fireImmuneItems = event.getDrops().stream().map(itemEntity -> new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemEntity.getItem()) {
                @Override
                public boolean fireImmune() {
                    return true;
                }
            }).collect(Collectors.toSet());

            event.getDrops().clear();
            event.getDrops().addAll(fireImmuneItems);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handleLightningBolt(final EntityStruckByLightningEvent event) {
        LightningBolt bolt = event.getLightning();
        Entity target = event.getEntity();

        if (bolt.getTags().contains(LIGHTNING_BOLT_TAG + "safe")) {
            if (target instanceof ItemEntity || target instanceof ExperienceOrb || bolt.getTags().contains(LIGHTNING_BOLT_TAG + target.getStringUUID())) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public void doPostAttack(@NotNull final LivingEntity attacker, @NotNull final Entity target, int level) {
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.clearFire();
        }
    }
}
