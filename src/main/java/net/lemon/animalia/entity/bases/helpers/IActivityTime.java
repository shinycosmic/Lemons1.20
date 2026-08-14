package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.entity.PathfinderMob;

public interface IActivityTime {
    /***
     * Returns the ActivityTime.
     * This is used in goal and move helpers as well as Sleep AI and spawning
     * @return
     */
    public ActivityTime activityTime();

    default boolean activityChecker(PathfinderMob mob) {
        boolean activityCheck = true;

        float light = mob.getLightLevelDependentMagicValue();

        switch (activityTime()) {
            case NOCTURNAL:
                activityCheck = light <= 0.5F;
                break;

            case DIURNAL:
                activityCheck = light >= 0.5F;
                break;

            case NONE:
                activityCheck = true;
                break;
        }

        // random override chance
        if (!activityCheck && mob.getRandom().nextFloat() < 0.25F) {
            return true;
        }

        return activityCheck;
    }
}
