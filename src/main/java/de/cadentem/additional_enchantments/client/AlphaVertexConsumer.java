package de.cadentem.additional_enchantments.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;

public class AlphaVertexConsumer implements VertexConsumer {
    private final VertexConsumer parent;
    private final float alphaPercent;

    public AlphaVertexConsumer(final VertexConsumer parent, final float alphaPercent) {
        this.parent = parent;
        this.alphaPercent = alphaPercent;
    }

    @Override
    public @NotNull VertexConsumer addVertex(final float x, final float y, final float z) {
        parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setColor(final int red, final int green, final int blue, final int originalAlpha) {
        parent.setColor(red, green, blue, (int) (originalAlpha * alphaPercent));
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv(final float u, final float v) {
        parent.setUv(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv1(final int u, final int v) {
        parent.setUv1(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv2(final int u, final int v) {
        parent.setUv2(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setNormal(final float x, final float y, final float z) {
        parent.setNormal(x, y, z);
        return this;
    }
}