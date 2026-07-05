package net.lemon.animalia.registry.events;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.client.screens.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if(event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
            Screen screen = Minecraft.getInstance().screen;
            if(screen instanceof HolonetHomeScreen
                || screen instanceof CompendiumHomeScreen
                || screen instanceof OrderListScreen
                || screen instanceof OrderGridScreen
                || screen instanceof CreatureDetailScreen) {
                event.setCanceled(true);
            }
        }
    }
}
