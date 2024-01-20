package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EntityTags extends EntityTypeTagsProvider {
    public static final TagKey<EntityType<?>> HOMING_BLACKLIST = new TagKey<>(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(AE.MODID, "homing_blacklist"));
    public static final TagKey<EntityType<?>> SHATTER_AOE_BLACKLIST = new TagKey<>(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(AE.MODID, "shatter_aoe_blacklist"));

    public EntityTags(final DataGenerator generator, @Nullable final ExistingFileHelper existingFileHelper) {
        super(generator, AE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(HOMING_BLACKLIST).add(EntityType.VILLAGER);
        tag(SHATTER_AOE_BLACKLIST).add(EntityType.VILLAGER);
    }
}
