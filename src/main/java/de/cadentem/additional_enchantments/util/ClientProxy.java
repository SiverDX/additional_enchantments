package de.cadentem.additional_enchantments.util;

import de.cadentem.additional_enchantments.client.AEClient;

public class ClientProxy implements Proxy {
    @Override
    public float getTimer() {
        return AEClient.timer;
    }
}
