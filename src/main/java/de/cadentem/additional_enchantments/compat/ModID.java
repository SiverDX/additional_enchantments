package de.cadentem.additional_enchantments.compat;

public enum ModID {
    IRIS("iris");

    private final String modid;

    ModID(final String modid) {
        this.modid = modid;
    }

    public String value() {
        return modid;
    }

    public boolean isLoaded() {
        return ModCheck.isModLoaded(modid);
    }
}
