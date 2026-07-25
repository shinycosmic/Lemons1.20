package net.lemon.animalia.registry.events;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.client.screens.*;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
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
                    || screen instanceof CreatureDetailScreen
                    || screen instanceof HolonetWelcomeScreen
                    || screen instanceof TutorialListScreen
                    || screen instanceof TutorialPageScreen) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (ModKeyBindings.OPEN_HOLONET.consumeClick()) {
            Player player = mc.player;
            Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i).is(ModItems.HOLONET.get())) {
                    HolonetScreenOpener.open();
                    return;
                }
            }
        }
    }
}
