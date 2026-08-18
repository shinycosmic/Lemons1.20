package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.entity.PathfinderMob;

public interface ICanSleep {
    int SLEEP_PHASE_NONE = 0;
    int SLEEP_PHASE_ENTERING = 1;
    int SLEEP_PHASE_SLEEPING = 2;
    int SLEEP_PHASE_EXITING = 3;

    int getSleepPhase();
    void setSleepPhase(int phase);

    int getCurrentSleepIdle();
    void setCurrentSleepIdle(int sleepIdleId);

    default boolean isAsleep() {
        return this.getSleepPhase() != SLEEP_PHASE_NONE;
    }

    default int getSleepIdleCount() {
        return 0;
    }

    default int getSleepIdleLength(int sleepIdleId) {
        return 20;
    }

    default int sleepIdleChance() {
        return 600;
    }

    default int getToSleepLength() {
        return 0;
    }

    default int getUnSleepLength() {
        return 0;
    }

    default int getSleepCooldown() {
        return 600;
    }

    default boolean canStartSleeping() {
        PathfinderMob mob = (PathfinderMob) this;
        return mob.onGround() && !mob.isInWater();
    }
}