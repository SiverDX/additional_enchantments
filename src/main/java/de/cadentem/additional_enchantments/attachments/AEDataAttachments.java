package de.cadentem.additional_enchantments.attachments;

import de.cadentem.additional_enchantments.AE;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AEDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, AE.MODID);

    // TODO :: check if should retained on death
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BlockVisionData>> BLOCK_VISION = REGISTRY.register("block_vision", () -> AttachmentType.serializable(BlockVisionData::new).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ClimbableData>> CLIMBABLE_DATA = REGISTRY.register("climbable_data", () -> AttachmentType.serializable(ClimbableData::new).copyOnDeath().build());
}
