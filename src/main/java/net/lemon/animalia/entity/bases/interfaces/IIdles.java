package net.lemon.animalia.entity.bases.interfaces;

import net.minecraft.world.entity.PathfinderMob;

/**
 * Capability for entities with cosmetic idle displays (fin flares, shimmers, stretches).
 * A species can have any number of displays (0-n); each is identified by a 0-based id
 * mapped to a GeckoLib triggerable animation in {@link #performIdleDisplay(int)}.
 *
 * <p>Displays trigger spontaneously via {@link #tickIdleDisplay(PathfinderMob)}. Only one
 * display can play at a time: GeckoLib animation state is client-side, so the server tracks
 * playback with a tick countdown fed by {@link #getIdleDisplayLength(int)}. All starts must
 * go through {@link #startIdleDisplay(int)}, which enforces the no-overlap rule.</p>
 *
 * <p>Implementors hold the countdown state via {@link #getIdleDisplayTicks()} /
 * {@link #setIdleDisplayTicks(int)}. Social propagation (schoolmates mimicking a display)
 * is layered on separately by the school signal system via
 * {@link #onSpontaneousIdleDisplay(int)}.</p>
 */
public interface IIdles {

    default int getIdleDisplayCount() {
        return 0;
    }

    /**
     * Perform idle display #displayId (0-based). Override to trigger GeckoLib
     * animations via triggerAnim on a controller with triggerable anims.
     * Do not call directly - use {@link #startIdleDisplay(int)}.
     */
    default void performIdleDisplay(int displayId) {
    }

    /** Length in ticks of display #displayId. */
    default int getIdleDisplayLength(int displayId) {
        return 40;
    }

    /** Remaining ticks of the currently playing display. */
    int getIdleDisplayTicks();

    void setIdleDisplayTicks(int ticks);

    default boolean isPlayingIdleDisplay() {
        return this.getIdleDisplayTicks() > 0;
    }

    /** Whether the entity is currently calm enough to play an idle display. */
    default boolean canPlayIdleDisplay() {
        return true;
    }

    default void onSpontaneousIdleDisplay(int displayId) {
    }

    default boolean startIdleDisplay(int displayId) {
        if (this.isPlayingIdleDisplay()) {
            return false;
        }
        this.performIdleDisplay(displayId);
        this.setIdleDisplayTicks(this.getIdleDisplayLength(displayId));
        return true;
    }

    default void tickIdleDisplay(PathfinderMob mob) {
        if (this.isPlayingIdleDisplay()) {
            this.setIdleDisplayTicks(this.getIdleDisplayTicks() - 1);
            return;
        }
        if (this.getIdleDisplayCount() <= 0 || !this.canPlayIdleDisplay()) {
            return;
        }
        if (mob.getRandom().nextInt(600) != 0) {
            return;
        }
        int display = mob.getRandom().nextInt(this.getIdleDisplayCount());
        if (this.startIdleDisplay(display)) {
            this.onSpontaneousIdleDisplay(display);
        }
    }
}