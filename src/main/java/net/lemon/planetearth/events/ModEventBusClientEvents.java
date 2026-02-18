package net.lemon.planetearth.events;

import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.entity.client.ModRenderer;
import net.lemon.planetearth.entity.model.OcellatedPampasSnakeModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlanetEarth.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //texture layer
    }
}
