package net.lemon.animalia.client.player.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientWelcomeCache {

    private static boolean hasSeen = false;

    public static void set(boolean seen) {
        hasSeen = seen;
    }

    public static void markSeen() {
        hasSeen = true;
    }

    public static boolean hasSeen() {
        return hasSeen;
    }

    public static void clear() {
        hasSeen = false;
    }
}