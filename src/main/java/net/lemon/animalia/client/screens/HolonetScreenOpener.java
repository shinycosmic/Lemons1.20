package net.lemon.animalia.client.screens;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HolonetScreenOpener {
    public static void open() {
        Minecraft.getInstance().setScreen(new HolonetHomeScreen());
    }
}