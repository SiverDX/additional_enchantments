package de.cadentem.additional_enchantments.data;

import de.cadentem.additional_enchantments.AE;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEEntityTags extends EntityTypeTagsProvider {
    public static final TagKey<EntityType<?>> HOMING_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AE.MODID, "homing_blacklist"));
    public static final TagKey<EntityType<?>> SHATTER_AOE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AE.MODID, "shatter_aoe_blacklist"));
    public static final TagKey<EntityType<?>> PERCEPTION_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AE.MODID, "perception_blacklist"));
    public static final TagKey<EntityType<?>> CONFUSION_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AE.MODID, "confusion_blacklist"));
    public static final TagKey<EntityType<?>> PLAGUE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AE.MODID, "plague_blacklist"));

    public AEEntityTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final ExistingFileHelper fileHelper) {
        super(output, lookupProvider, AE.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(HOMING_BLACKLIST)
                .add(EntityType.VILLAGER)
                .add(EntityType.IRON_GOLEM);

        tag(SHATTER_AOE_BLACKLIST)
                .add(EntityType.VILLAGER)
                .add(EntityType.IRON_GOLEM);

        tag(PERCEPTION_BLACKLIST);

        tag(CONFUSION_BLACKLIST);

        tag(PLAGUE_BLACKLIST).add(EntityType.VILLAGER);
    }
}
