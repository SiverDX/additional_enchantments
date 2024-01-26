package de.cadentem.additional_enchantments.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue CACHE_EXPIRE;

    static {
        BUILDER.push("ore_sight");
        CACHE_EXPIRE = BUILDER.comment("Determines after how many seconds cached entries expire (affects how fast blocks update their outline) (0 disables caching - not recommended)").defineInRange("cache_expire", 2, 0, 10);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
