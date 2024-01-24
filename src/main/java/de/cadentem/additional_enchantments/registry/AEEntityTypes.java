package de.cadentem.additional_enchantments.registry;

import de.cadentem.additional_enchantments.AE;
import de.cadentem.additional_enchantments.core.entity.ShardArrow;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AEEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AE.MODID);

    public static final RegistryObject<EntityType<ShardArrow>> SHARD_ARROW = ENTITY_TYPES.register("shard_arrow", () ->
            EntityType.Builder.<ShardArrow>of(ShardArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(AE.MODID + ":" + "shard_arrow")
    );
}
