package de.cadentem.additional_enchantments.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue CACHE_KEPT_SECONDS;

    static {
        BUILDER.push("ore_sight");
        CACHE_KEPT_SECONDS = BUILDER.comment("Determines for how long blocks are cached (affects how fast blocks update their outline) (0 disables caching)").defineInRange("cache_kept_seconds", 2, 0, 10);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
