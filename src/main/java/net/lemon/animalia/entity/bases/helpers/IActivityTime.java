package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.entity.PathfinderMob;

public interface IActivityTime {

    public ActivityTime activityTime();

    default boolean isActiveTime(PathfinderMob mob) {
        float light = mob.getLightLevelDependentMagicValue();

        return switch (activityTime()) {
            case NOCTURNAL -> light <= 0.5F;
            case DIURNAL -> light >= 0.5F;
            default -> true;
        };
    }

    default boolean activityChecker(PathfinderMob mob) {
        boolean activityCheck = this.isActiveTime(mob);

        // random override chance
        if (!activityCheck && mob.getRandom().nextFloat() < 0.25F) {
            return true;
        }

        return activityCheck;
    }
}
