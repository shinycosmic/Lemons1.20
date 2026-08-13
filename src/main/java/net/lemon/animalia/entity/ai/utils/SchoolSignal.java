package net.lemon.animalia.entity.ai.utils;

public enum SchoolSignal {
    IDLE_DISPLAY,
    GRAZE,
    HIDE,
    REST;

    public int getBaseDelay() {
        return switch (this) {
            case IDLE_DISPLAY, REST -> 10;
            case GRAZE -> 20;
            case HIDE -> 15;
        };
    }

    public int getDelay() {
        return switch (this) {
            case IDLE_DISPLAY, HIDE -> 30;
            case GRAZE -> 40;
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

    public boolean wavePropagates() {
        return switch (this) {
            case IDLE_DISPLAY, GRAZE, REST -> false;
            case HIDE -> true;
        };
    }
}