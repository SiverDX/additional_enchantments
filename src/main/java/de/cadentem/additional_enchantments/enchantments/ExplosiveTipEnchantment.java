package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.CapabilityProvider;
import de.cadentem.additional_enchantments.core.interfaces.ExplosionAccess;
import de.cadentem.additional_enchantments.core.interfaces.ProjectileAccess;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.enchantments.base.EnchantmentCategories;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ExplosiveTipEnchantment extends ConfigurableEnchantment {
    public ExplosiveTipEnchantment() {
        super(Rarity.RARE, EnchantmentCategories.RANGED, EquipmentSlot.MAINHAND, AEEnchantments.EXPLOSIVE_TIP_ID);
    }

    @SubscribeEvent
    public static void markProjectile(final EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof LivingEntity livingOwner) {
                int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.EXPLOSIVE_TIP.get());
                ((ProjectileAccess) projectile).additional_enchantments$setExplosiveTipEnchantmentLevel(level);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void triggerExplosion(final ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();

        if (event.isCanceled() || !(projectile.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        int enchantmentLevel = ((ProjectileAccess) projectile).additional_enchantments$getExplosiveTipEnchantmentLevel();

        if (enchantmentLevel > 0) {
            if (!(projectile.getOwner() instanceof LivingEntity livingOwner)) {
                return;
            }

            CapabilityProvider.getCapability(livingOwner).ifPresent(configuration -> {
                Explosion explosion = new Explosion(serverLevel, projectile, null, null, projectile.getX(), projectile.getY(), projectile.getZ(), enchantmentLevel, false, configuration.explosionType);
                ((ExplosionAccess) explosion).additional_enchantments$setWasTriggeredByEnchantment(true);

                if (ForgeEventFactory.onExplosionStart(projectile.getLevel(), explosion)) {
                    return;
                }

                explosion.explode();
                explosion.finalizeExplosion(true);

                if (configuration.explosionType == Explosion.BlockInteraction.NONE) {
                    explosion.clearToBlow();
                }

                for (ServerPlayer serverPlayer : serverLevel.players()) {
                    if (serverPlayer.distanceToSqr(projectile.getX(), projectile.getY(), projectile.getZ()) < 4096) {
                        serverPlayer.connection.send(new ClientboundExplodePacket(projectile.getX(), projectile.getY(), projectile.getZ(), enchantmentLevel, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
                    }
                }

                projectile.discard();
            });
        }
    }

    @SubscribeEvent
    public static void handleExplosion(final ExplosionEvent.Detonate event) {
        if (((ExplosionAccess) event.getExplosion()).additional_enchantments$wasTriggeredByEnchantment()) {
            event.getAffectedEntities().removeIf(entity -> entity instanceof ExperienceOrb || entity instanceof ItemEntity || entity == event.getExplosion().getSourceMob());
        }
    }
}
