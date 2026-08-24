package de.cadentem.additional_enchantments.enchantments.climbing;

import de.cadentem.additional_enchantments.util.IBoundingBoxOffset;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CeilingClimbDimensions {
    /** Crawling / Swimming hit box height */
    public static final float HEIGHT = 0.2f;

    /** Distance from the ceiling to the eye */
    private static final float CEILING_DISTANCE = 0.1f;

    /** Adjust the height of the dimension for ceiling crawling */
    public static EntityDimensions adjust(final EntityDimensions dimensions) {
        return EntityDimensions.fixed(dimensions.width(), HEIGHT).withEyeHeight(dimensions.height() - CEILING_DISTANCE);
    }

    /** Amount the bounding box needs to be raised by to keep its top at the ceiling */
    public static double adjustOffset(final EntityDimensions dimensions) {
        return dimensions.height() - HEIGHT;
    }

    /**
     * Either returns the original {@link Entity#getBbHeight()} </br>
     * Or the offset + {@link #HEIGHT}, which results in the original height
     */
    public static double getUnmodifiedHeight(final LivingEntity entity) {
        double offset = ((IBoundingBoxOffset) entity).additional_enchantments$getBoundingBoxOffset();
        double height;

        if (offset == 0) {
            height = entity.getBbHeight();
        } else {
            height = offset + HEIGHT;
        }

        return height / entity.getScale();
    }
}
