package de.cadentem.additional_enchantments.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue GROUPED_RENDER_RANGE;
    public static final ForgeConfigSpec.IntValue CACHE_KEPT_SECONDS;

    static {
        BUILDER.push("Ore Sight");
        GROUPED_RENDER_RANGE = BUILDER.comment("The range (in blocks) at which the [Ore Sight] enchantment will render blocks as a group - decrease this value if you encounter performance issues").defineInRange("grouped_render_range", 20, 0, 50);
        CACHE_KEPT_SECONDS = BUILDER.comment("Determines for how long things are cached regarding [Ore Sight] enchantment outline rendering (affects how fast blocks update their outline)").defineInRange("cache_kept_seconds", 2, 0, 15);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
