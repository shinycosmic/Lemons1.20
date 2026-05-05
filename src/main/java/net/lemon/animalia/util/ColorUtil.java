package net.lemon.animalia.util;

import net.lemon.animalia.entity.custom.traits.BettaTraits;

public enum ColorUtil {
    NONE(0,0xFFFFFF),
    BLUE(1,0x1976D2),
    YELLOW(2,0xFBC02D),
    WHITE(3,0xE6D6D6),
    BLACK(4,0x212121),
    ORANGE(5,0xB55B00),
    TURQUOISE(6,0x06CFC5),
    GREEN(7,0x5FC276),
    LAVENDER(8,0x9575CD),
    PURPLE(9,0x2F1759),
    PINK(10,0xF282A8),
    BROWN(11,0x6D4C41),
    RED(12,0xD32F2F);
    public final int rgb;
    public final int id;

    ColorUtil(int id, int rgb) {
        this.id = id;
        this.rgb = rgb;
    }

    public int getId() {
        return id;
    }

    public static ColorUtil fromId(int id) {
        for (ColorUtil c : values()) {
            if (c.id == id) return c;
        }
        return NONE;
    }

    public float r() { return ((rgb >> 16) & 255) / 255f; }
    public float g() { return ((rgb >> 8) & 255) / 255f; }
    public float b() { return (rgb & 255) / 255f; }

}
