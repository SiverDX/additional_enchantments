package de.cadentem.additional_enchantments.client;

import de.cadentem.additional_enchantments.util.Proxy;

public class ClientProxy implements Proxy {
    @Override
    public float getTimer() {
        return AEClient.TIMER;
    }
}
