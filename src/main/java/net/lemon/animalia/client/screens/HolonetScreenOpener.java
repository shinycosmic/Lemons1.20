package net.lemon.animalia.client.screens;

import net.lemon.animalia.client.player.network.ClientWelcomeCache;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HolonetScreenOpener {
    public static void open() {
        if (!ClientWelcomeCache.hasSeen()) {
            Minecraft.getInstance().setScreen(new HolonetWelcomeScreen());
        } else {
            Minecraft.getInstance().setScreen(new HolonetHomeScreen());
        }
    }
}