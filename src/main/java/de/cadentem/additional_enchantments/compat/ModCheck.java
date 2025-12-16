package de.cadentem.additional_enchantments.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Separate class to make sure we're not loading any unnecessary classes when mixins are being initialized */
public class ModCheck {
    private static final Map<String, List<String>> ALIAS = Map.of(
            Mod.IRIS.modid(), List.of("oculus")
    );

    private static final Map<String, Boolean> MODS = new HashMap<>();

    private static boolean isModLoaded(final String mod) {
        return MODS.computeIfAbsent(mod, key -> {
            if (check(key)) {
                return true;
            }

            for (String alias : ALIAS.getOrDefault(key, List.of())) {
                if (check(alias)) {
                    return true;
                }
            }

            return false;
        });
    }

    private static boolean check(final String modid) {
        ModList modList = ModList.get();

        if (modList != null && modList.isLoaded(modid)) {
            return true;
        }

        return LoadingModList.get().getModFileById(modid) != null;
    }

    public enum Mod {
        IRIS("iris");

        private final String modid;

        Mod(final String modid) {
            this.modid = modid;
        }

        public String modid() {
            return modid;
        }

        public boolean isLoaded() {
            return ModCheck.isModLoaded(modid);
        }
    }
}
