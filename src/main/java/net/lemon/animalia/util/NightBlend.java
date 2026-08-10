package net.lemon.animalia.util;

import net.minecraft.world.entity.Entity;

public final class NightBlend {

    private static final long NIGHT_START = 13000L;
    private static final long NIGHT_END = 23000L;
    private static final long FADE_TICKS = 100L;
    private static final long STAGGER_TICKS = 600L;

    private NightBlend() {
    }

    /** Night-texture blend for this entity: 0 = day, 1 = night, between = fading. */
    public static float blend(Entity entity) {
        long offset = (entity.getId() * 1664525L & 0x7FFFFFFFL) % STAGGER_TICKS;
        long time = (entity.level().getDayTime() + offset) % 24000L;
        if (time < NIGHT_START || time >= NIGHT_END + FADE_TICKS) {
            return 0.0F;
        }
        if (time < NIGHT_START + FADE_TICKS) {
            return (time - NIGHT_START) / (float) FADE_TICKS;
        }
        if (time < NIGHT_END) {
            return 1.0F;
        }
        return 1.0F - (time - NIGHT_END) / (float) FADE_TICKS;
    }

    public static boolean isFullNight(Entity entity) {
        return blend(entity) >= 1.0F;
    }
}