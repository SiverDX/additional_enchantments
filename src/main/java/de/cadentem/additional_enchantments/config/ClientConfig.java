package de.cadentem.additional_enchantments.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue GROUPED_RENDER_RANGE;

    static {
        GROUPED_RENDER_RANGE = BUILDER.comment("The range (in blocks) at which the [Ore Sight] enchantment will render blocks as a group - decrease this value if you encounter performance issues").defineInRange("grouped_render_range", 20, 0, 30);

        SPEC = BUILDER.build();
    }
}
