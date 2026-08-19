package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.entity.PathfinderMob;

public interface IIdles {

    enum IdleType {
        TWITCH,
        MOVEMENT_POSITIVE,
        MOVEMENT_NEGATIVE
    }

    default int getIdleCount() {
        return 0;
    }

    default IdleType getIdleType(int displayId) {
        return IdleType.MOVEMENT_POSITIVE;
    }

    //override this with a switch to control idle lengths per idle
    default int getIdleLength(int displayId) {
        return 40;
    }

    int getIdleTicks();

    void setIdleTicks(int ticks);

    int getTwitchTicks();

    void setTwitchTicks(int ticks);

    int getCurrRegIdle();

    void setCurrRegIdle(int displayId);

    int getCurrTwitchIdle();

    void setCurrTwitchIdle(int displayId);

    default boolean isPlayingIdle() {
        return this.getIdleTicks() > 0;
    }

    default boolean isPlayingPositiveIdle() {
        int body = this.getCurrRegIdle();
        return body >= 0 && this.getIdleType(body) == IdleType.MOVEMENT_POSITIVE;
    }

    default boolean isMovementLockedByIdle() {
        return this.getCurrRegIdle() >= 0
                && this.getIdleType(this.getCurrRegIdle()) == IdleType.MOVEMENT_NEGATIVE;
    }

    default int twitchChance() {
        return 200;
    }

    default int regChance() {
        return 300;
    }

    default boolean canPlayIdle() {
        if (this instanceof ICanThreat threat && threat.isThreatening()) {
            return false;
        }
        if (this instanceof ICanGuard guard && guard.isGuarding()) {
            return false;
        }
        if (this instanceof ICanSleep sleeper && sleeper.isAsleep()) {
            return false;
        }
        return true;
    }

    default boolean isAtRest(PathfinderMob mob) {
        return mob.getNavigation().isDone();
    }

    default void onMovementLockingIdleStart() {
    }

    default void onMovementLockingIdleEnd() {
    }

    default void onRandomIdle(int displayId) {
    }

    default int pickIdleOfType(PathfinderMob mob, IdleType type) {
        int matches = 0;
        for (int id = 0; id < this.getIdleCount(); id++) {
            if (this.getIdleType(id) == type) {
                matches++;
            }
        }
        if (matches == 0) {
            return -1;
        }
        int pick = mob.getRandom().nextInt(matches);
        for (int id = 0; id < this.getIdleCount(); id++) {
            if (this.getIdleType(id) == type && pick-- == 0) {
                return id;
            }
        }
        return -1;
    }

    default boolean startIdle(int displayId) {
        IdleType type = this.getIdleType(displayId);
        if (type == IdleType.TWITCH) {
            if (this.getCurrTwitchIdle() >= 0 || this.isPlayingPositiveIdle()) {
                return false;
            }
            this.setCurrTwitchIdle(displayId);
            this.setTwitchTicks(this.getIdleLength(displayId));
            return true;
        }
        if (this.getCurrRegIdle() >= 0) {
            return false;
        }
        if (type == IdleType.MOVEMENT_POSITIVE && this.getCurrTwitchIdle() >= 0) {
            return false;
        }
        this.setCurrRegIdle(displayId);
        this.setIdleTicks(this.getIdleLength(displayId));
        if (type == IdleType.MOVEMENT_NEGATIVE) {
            ((PathfinderMob) this).getNavigation().stop();
            this.onMovementLockingIdleStart();
        }
        return true;
    }

    default void tickIdle(PathfinderMob mob) {
        if (this.getIdleTicks() > 0) {
            boolean locked = this.isMovementLockedByIdle();
            if (locked) {
                mob.getNavigation().stop();
            }
            this.setIdleTicks(this.getIdleTicks() - 1);
            if (this.getIdleTicks() <= 0) {
                this.setCurrRegIdle(-1);
                if (locked) {
                    this.onMovementLockingIdleEnd();
                }
            }
        }
        if (this.getTwitchTicks() > 0) {
            this.setTwitchTicks(this.getTwitchTicks() - 1);
            if (this.getTwitchTicks() <= 0) {
                this.setCurrTwitchIdle(-1);
            }
        }

        if (this.getIdleCount() <= 0 || !this.canPlayIdle()) {
            return;
        }

        if (this.getCurrTwitchIdle() < 0 && !this.isPlayingPositiveIdle()
                && mob.getRandom().nextInt(twitchChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.TWITCH);
            if (display >= 0) {
                this.startIdle(display);
            }
        }

        if (this.getCurrRegIdle() >= 0) {
            return;
        }
        if (this.isAtRest(mob) && mob.getRandom().nextInt(regChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_NEGATIVE);
            if (display >= 0 && this.startIdle(display)) {
                return;
            }
        }
        if (this.getCurrTwitchIdle() < 0 && mob.getRandom().nextInt(regChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_POSITIVE);
            if (display >= 0 && this.startIdle(display)) {
                this.onRandomIdle(display);
            }
        }
    }
}