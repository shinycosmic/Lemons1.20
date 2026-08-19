package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.world.entity.PathfinderMob;

public interface IIdles {

    enum IdleType {
        TWITCH,
        MOVEMENT_POSITIVE,
        MOVEMENT_NEGATIVE
    }

    default int getIdleDisplayCount() {
        return 0;
    }

    default IdleType getIdleType(int displayId) {
        return IdleType.MOVEMENT_POSITIVE;
    }

    //override this with a switch to control idle lengths per idle
    default int getIdleDisplayLength(int displayId) {
        return 40;
    }

    int getIdleDisplayTicks();

    void setIdleDisplayTicks(int ticks);

    int getTwitchIdleTicks();

    void setTwitchIdleTicks(int ticks);

    int getCurrentBodyIdle();

    void setCurrentBodyIdle(int displayId);

    int getCurrentTwitchIdle();

    void setCurrentTwitchIdle(int displayId);

    default boolean isPlayingIdleDisplay() {
        return this.getIdleDisplayTicks() > 0;
    }

    default boolean isPlayingPositiveIdle() {
        int body = this.getCurrentBodyIdle();
        return body >= 0 && this.getIdleType(body) == IdleType.MOVEMENT_POSITIVE;
    }

    default boolean isMovementLockedByIdle() {
        return this.getCurrentBodyIdle() >= 0
                && this.getIdleType(this.getCurrentBodyIdle()) == IdleType.MOVEMENT_NEGATIVE;
    }

    default int twitchChance() {
        return 200;
    }

    default int bodyChance() {
        return 300;
    }

    default boolean canPlayIdleDisplay() {
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

    default boolean isAtRestForIdle(PathfinderMob mob) {
        return mob.getNavigation().isDone();
    }

    default void onMovementLockingIdleStart() {
    }

    default void onMovementLockingIdleEnd() {
    }

    default void onSpontaneousIdleDisplay(int displayId) {
    }

    default int pickIdleOfType(PathfinderMob mob, IdleType type) {
        int matches = 0;
        for (int id = 0; id < this.getIdleDisplayCount(); id++) {
            if (this.getIdleType(id) == type) {
                matches++;
            }
        }
        if (matches == 0) {
            return -1;
        }
        int pick = mob.getRandom().nextInt(matches);
        for (int id = 0; id < this.getIdleDisplayCount(); id++) {
            if (this.getIdleType(id) == type && pick-- == 0) {
                return id;
            }
        }
        return -1;
    }

    default boolean startIdleDisplay(int displayId) {
        IdleType type = this.getIdleType(displayId);
        if (type == IdleType.TWITCH) {
            if (this.getCurrentTwitchIdle() >= 0 || this.isPlayingPositiveIdle()) {
                return false;
            }
            this.setCurrentTwitchIdle(displayId);
            this.setTwitchIdleTicks(this.getIdleDisplayLength(displayId));
            return true;
        }
        if (this.getCurrentBodyIdle() >= 0) {
            return false;
        }
        if (type == IdleType.MOVEMENT_POSITIVE && this.getCurrentTwitchIdle() >= 0) {
            return false;
        }
        this.setCurrentBodyIdle(displayId);
        this.setIdleDisplayTicks(this.getIdleDisplayLength(displayId));
        if (type == IdleType.MOVEMENT_NEGATIVE) {
            ((PathfinderMob) this).getNavigation().stop();
            this.onMovementLockingIdleStart();
        }
        return true;
    }

    default void tickIdleDisplay(PathfinderMob mob) {
        if (this.getIdleDisplayTicks() > 0) {
            boolean locked = this.isMovementLockedByIdle();
            if (locked) {
                mob.getNavigation().stop();
            }
            this.setIdleDisplayTicks(this.getIdleDisplayTicks() - 1);
            if (this.getIdleDisplayTicks() <= 0) {
                this.setCurrentBodyIdle(-1);
                if (locked) {
                    this.onMovementLockingIdleEnd();
                }
            }
        }
        if (this.getTwitchIdleTicks() > 0) {
            this.setTwitchIdleTicks(this.getTwitchIdleTicks() - 1);
            if (this.getTwitchIdleTicks() <= 0) {
                this.setCurrentTwitchIdle(-1);
            }
        }

        if (this.getIdleDisplayCount() <= 0 || !this.canPlayIdleDisplay()) {
            return;
        }

        if (this.getCurrentTwitchIdle() < 0 && !this.isPlayingPositiveIdle()
                && mob.getRandom().nextInt(twitchChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.TWITCH);
            if (display >= 0) {
                this.startIdleDisplay(display);
            }
        }

        if (this.getCurrentBodyIdle() >= 0) {
            return;
        }
        if (this.isAtRestForIdle(mob) && mob.getRandom().nextInt(bodyChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_NEGATIVE);
            if (display >= 0 && this.startIdleDisplay(display)) {
                return;
            }
        }
        if (this.getCurrentTwitchIdle() < 0 && mob.getRandom().nextInt(bodyChance()) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_POSITIVE);
            if (display >= 0 && this.startIdleDisplay(display)) {
                this.onSpontaneousIdleDisplay(display);
            }
        }
    }
}