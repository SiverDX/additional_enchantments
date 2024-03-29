package de.cadentem.additional_enchantments.enchantments;

import de.cadentem.additional_enchantments.capability.PlayerDataProvider;
import de.cadentem.additional_enchantments.capability.ProjectileDataProvider;
import de.cadentem.additional_enchantments.config.ServerConfig;
import de.cadentem.additional_enchantments.core.interfaces.ExplosionAccess;
import de.cadentem.additional_enchantments.enchantments.base.AEEnchantmentCategory;
import de.cadentem.additional_enchantments.enchantments.base.ConfigurableEnchantment;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class ExplosiveTipEnchantment extends ConfigurableEnchantment {
    public ExplosiveTipEnchantment() {
        super(Rarity.RARE, AEEnchantmentCategory.RANGED_AND_TRIDENT, EquipmentSlot.MAINHAND, AEEnchantments.EXPLOSIVE_TIP_ID);
    }

    @Override
    protected boolean checkCompatibility(@NotNull final Enchantment other) {
        return other != Enchantments.RIPTIDE && super.checkCompatibility(other);
    }

    public static void setEnchantmentLevel(final Projectile projectile) {
        if (projectile.getOwner() instanceof LivingEntity livingOwner) {
            int level = livingOwner.getMainHandItem().getEnchantmentLevel(AEEnchantments.EXPLOSIVE_TIP.get());

            if (level > 0) {
                ProjectileDataProvider.getCapability(projectile).ifPresent(data -> data.explosiveTipEnchantmentLevel = level);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void triggerExplosion(final ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();

        if (event.isCanceled() || !(projectile.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        ProjectileDataProvider.getCapability(projectile).ifPresent(projectileData -> {
            if (projectileData.explosiveTipEnchantmentLevel > 0) {
                if (!(projectile.getOwner() instanceof LivingEntity livingOwner)) {
                    return;
                }

                float radius = Math.max(0.1f, projectileData.explosiveTipEnchantmentLevel * ServerConfig.EXPLOSIVE_TIP_RADIUS_MULTIPLIER.get().floatValue());

                PlayerDataProvider.getCapability(livingOwner).ifPresent(playerData -> {
                    Explosion explosion = new Explosion(serverLevel, projectile, null, null, projectile.getX(), projectile.getY(), projectile.getZ(), radius, false, playerData.explosionType);
                    ((ExplosionAccess) explosion).additional_enchantments$setWasTriggeredByEnchantment(true);

                    if (ForgeEventFactory.onExplosionStart(projectile.getLevel(), explosion)) {
                        return;
                    }

                    explosion.explode();
                    explosion.finalizeExplosion(true);

                    if (playerData.explosionType == Explosion.BlockInteraction.NONE) {
                        explosion.clearToBlow();
                    }

                    for (ServerPlayer serverPlayer : serverLevel.players()) {
                        if (serverPlayer.distanceToSqr(projectile.getX(), projectile.getY(), projectile.getZ()) < 4096) {
                            serverPlayer.connection.send(new ClientboundExplodePacket(projectile.getX(), projectile.getY(), projectile.getZ(), radius, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
                        }
                    }

                    projectileData.explosiveTipEnchantmentLevel = 0;

                    if (playerData.explosionType == Explosion.BlockInteraction.BREAK && event.getRayTraceResult().getType() == HitResult.Type.BLOCK) {
                        // Otherwise the projectile will just keep falling down, hitting a block and resetting to its initial fall position
                        event.setCanceled(true);
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public static void handleExplosion(final ExplosionEvent.Detonate event) {
        if (((ExplosionAccess) event.getExplosion()).additional_enchantments$wasTriggeredByEnchantment()) {
            LivingEntity source = event.getExplosion().getSourceMob();

            event.getAffectedEntities().removeIf(entity -> {
                if (entity == source) {
                    return true;
                }

                if (entity instanceof ExperienceOrb || entity instanceof ItemEntity) {
                    return true;
                }

                if (source != null) {
                    if (entity instanceof TamableAnimal animal && animal.isOwnedBy(source)) {
                        return true;
                    }

                    return entity.isAlliedTo(source);
                }

                return false;
            });
        }
    }
}
