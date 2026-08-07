package net.lemon.animalia.entity.bases.interfaces;

import net.minecraft.world.entity.PathfinderMob;

/**
 * Capability for entities with cosmetic idle displays, split into three types (see
 * {@link IdleType}): twitches (blinks, tongue flicks) layer over everything on their own
 * bones, movement-positive idles (scratches, yawns, fin flares) play safely during
 * movement, and movement-negative idles (digging, meerkat look-arounds) lock movement for
 * their duration via a travel() gate in the bases plus a per-tick navigation stop.
 *
 * <p>Delivery is state-based: the playing ids are synced entity data
 * ({@link #getCurrentBodyIdle()} / {@link #getCurrentTwitchIdle()}, -1 = none) and client
 * predicates branch on them, playing animations named {@code idle0..idleN}. Cancellation
 * is resetting the synced id - there are no GeckoLib triggers.</p>
 *
 * <p>Two channels run concurrently: twitch (TWITCH ids) and body (MOVEMENT_POSITIVE and
 * MOVEMENT_NEGATIVE ids). Each channel plays one idle at a time, tracked server-side by
 * tick countdowns fed by {@link #getIdleDisplayLength(int)}. Twitch and movement-positive
 * idles share one animation controller, so they are mutually exclusive at play time. All
 * starts must go through {@link #startIdleDisplay(int)}, which enforces both rules.</p>

 * <p>Spontaneous rolls happen in {@link #tickIdleDisplay(PathfinderMob)}, gated by
 * {@link #canPlayIdleDisplay()} (no hide, threat, or guard phase active). Social
 * propagation (schoolmates mimicking a display) is layered on separately by the school
 * signal system via {@link #onSpontaneousIdleDisplay(int)} and applies to
 * movement-positive idles only.</p>
 */
public interface IIdles {

    int TWITCH_CHANCE = 200;
    int BODY_CHANCE = 600;

    enum IdleType {
        TWITCH,
        MOVEMENT_POSITIVE,
        MOVEMENT_NEGATIVE
    }

    default int getIdleDisplayCount() {
        return 0;
    }

    /** Classification of display #displayId (0-based). Species map each id to a type. */
    default IdleType getIdleType(int displayId) {
        return IdleType.MOVEMENT_POSITIVE;
    }

    /**
     * Length in ticks of display #displayId. Lengths are arbitrary per id and must match
     * the actual animation length by hand.
     */
    default int getIdleDisplayLength(int displayId) {
        return 40;
    }

    /** Remaining ticks of the currently playing body idle. */
    int getIdleDisplayTicks();

    void setIdleDisplayTicks(int ticks);

    /** Remaining ticks of the currently playing twitch idle. */
    int getTwitchIdleTicks();

    void setTwitchIdleTicks(int ticks);

    /** Synced id of the playing body idle (POSITIVE or NEGATIVE), -1 = none. */
    int getCurrentBodyIdle();

    void setCurrentBodyIdle(int displayId);

    /** Synced id of the playing twitch idle, -1 = none. */
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

    /**
     * Whether the entity is currently calm enough to play an idle display. Aggregates the
     * phased modes: threat and guard here, hiding added by the base overrides. Species
     * without a system pass vacuously.
     */
    default boolean canPlayIdleDisplay() {
        if (this instanceof ICanThreat threat && threat.isThreatening()) {
            return false;
        }
        if (this instanceof ICanGuard guard && guard.isGuarding()) {
            return false;
        }
        return true;
    }

    /** At-rest gate for movement-negative idles. Land bases add an onGround check. */
    default boolean isAtRestForIdle(PathfinderMob mob) {
        return mob.getNavigation().isDone();
    }

    /** Called when a movement-negative idle starts. Fish leave their school here. */
    default void onMovementLockingIdleStart() {
    }

    /** Called when a movement-negative idle ends or is cancelled by hurt. */
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
                && mob.getRandom().nextInt(TWITCH_CHANCE) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.TWITCH);
            if (display >= 0) {
                this.startIdleDisplay(display);
            }
        }

        if (this.getCurrentBodyIdle() >= 0) {
            return;
        }
        if (this.isAtRestForIdle(mob) && mob.getRandom().nextInt(BODY_CHANCE) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_NEGATIVE);
            if (display >= 0 && this.startIdleDisplay(display)) {
                return;
            }
        }
        if (this.getCurrentTwitchIdle() < 0 && mob.getRandom().nextInt(BODY_CHANCE) == 0) {
            int display = this.pickIdleOfType(mob, IdleType.MOVEMENT_POSITIVE);
            if (display >= 0 && this.startIdleDisplay(display)) {
                this.onSpontaneousIdleDisplay(display);
            }
        }
    }
}