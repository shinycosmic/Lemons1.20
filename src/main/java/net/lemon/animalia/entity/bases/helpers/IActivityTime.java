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

    default boolean isActiveWindow(PathfinderMob mob) {
        long time = mob.level().getDayTime() % 24000L;
        long half = 1800L + (mob.getId() * 1664525L & 0x7FFFFFFFL) % 1200L;
        return switch (activityTime()) {
            case NOCTURNAL -> mob.level().isNight() || this.isSoftActivity() && Math.abs(time - 6000L) >= half;
            case DIURNAL -> mob.level().isDay() || this.isSoftActivity() && Math.abs(time - 18000L) >= half;
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

    default boolean isSoftActivity() {
        return false;
    }
}
