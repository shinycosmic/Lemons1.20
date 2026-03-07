package net.lemon.animalia.events;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.ToothfishEntity;
import net.lemon.animalia.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    /***
     * register Attributes here. You set them in the entity and here is where you build them.
     * @param event
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CHILEANSEABASS.get(), ToothfishEntity.setAttributes());
        event.put(ModEntities.ELEGINOPS_MACLOVINUS.get(), ToothfishEntity.setAttributes());
    }
}
