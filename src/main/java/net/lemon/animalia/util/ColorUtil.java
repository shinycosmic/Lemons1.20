package net.lemon.animalia.util;

import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.minecraft.util.RandomSource;

public enum ColorUtil {
    NONE(0,0xFFFFFF),
    BLUE(1,0x0080FF),
    YELLOW(2,0xFFB909),
    WHITE(3,0xF1F1F1),
    BLACK(4,0x212121),
    ORANGE(5,0xFF9900),
    TURQUOISE(6,0x06CFC5),
    GREEN(7,0x57820C),
    LAVENDER(8,0x9575CD),
    PURPLE(9,0x2F1759),
    PINK(10,0xFF5690),
    BROWN(11,0x4F2519),
    RED(12,0xFF0000);
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

    public static ColorUtil randomNonNone(RandomSource rand) {
        ColorUtil[] values = ColorUtil.values();
        return values[1 + rand.nextInt(values.length - 1)];
    }

}
