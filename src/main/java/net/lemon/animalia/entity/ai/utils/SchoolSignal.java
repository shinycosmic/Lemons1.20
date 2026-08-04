package net.lemon.animalia.entity.ai.utils;

public enum SchoolSignal {

    /**
     * A cosmetic idle animation (fin flare, body shimmer, etc).
     * Low prerequisite - any fish can perform this if it has the ability to
     */
    IDLE_DISPLAY,

    /**
     * Grazing on a block (coral, algae, sponge, etc).
     * Requires a valid graze target nearby - fish that can't find one ignore the signal.
     */
    GRAZE,

    /**
     * Burrowing/hiding in the substrate.
     * Requires ground contact and a valid block below - fish that can't hide ignore the signal.
     */
    HIDE,

    /**
     * Resting at the current position (hovering in place, reduced movement).
     * Low prerequisite - mainly requires the school to be moving slowly.
     */
    REST;

    public int getBaseDelay() {
        return switch (this) {
            case IDLE_DISPLAY -> 10;
            case GRAZE -> 20;
            case HIDE -> 15;
            case REST -> 10;
        };
    }

    public int getDelayJitter() {
        return switch (this) {
            case IDLE_DISPLAY -> 30;
            case GRAZE -> 40;
            case HIDE -> 30;
            case REST -> 20;
        };
    }

    public float getAdoptionChance() {
        return switch (this) {
            case IDLE_DISPLAY -> 0.75f;
            case GRAZE -> 0.7f;
            case HIDE -> 0.85f;
            case REST -> 0.6f;
        };
    }

    /**
     * Whether this signal should re-broadcast when a fish adopts the behavior.
     * True = wave continues outward. False = only the originator triggers neighbors.
     */
    public boolean wavePropagates() {
        return switch (this) {
            case IDLE_DISPLAY, GRAZE, REST -> false;
            case HIDE -> true;
        };
    }
}