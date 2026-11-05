package net.lemon.animalia.util;

import net.lemon.animalia.Animalia;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = Animalia.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IdRemapper {
    public static final String MODID = "animalia";
    private static final String PREFIX = MODID + ":";

    private IdRemapper() {
    }

    public static String migrate(String id) {
        return id.startsWith(PREFIX) ? Animalia.MODID + ":" + id.substring(PREFIX.length()) : id;
    }

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        remap(event, ForgeRegistries.Keys.BLOCKS, ForgeRegistries.BLOCKS);
        remap(event, ForgeRegistries.Keys.ITEMS, ForgeRegistries.ITEMS);
        remap(event, ForgeRegistries.Keys.ENTITY_TYPES, ForgeRegistries.ENTITY_TYPES);
        remap(event, ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, ForgeRegistries.BLOCK_ENTITY_TYPES);
        remap(event, ForgeRegistries.Keys.POI_TYPES, ForgeRegistries.POI_TYPES);
        remap(event, ForgeRegistries.Keys.VILLAGER_PROFESSIONS, ForgeRegistries.VILLAGER_PROFESSIONS);
        remap(event, ForgeRegistries.Keys.SOUND_EVENTS, ForgeRegistries.SOUND_EVENTS);
        remap(event, ForgeRegistries.Keys.MENU_TYPES, ForgeRegistries.MENU_TYPES);
        remap(event, ForgeRegistries.Keys.FEATURES, ForgeRegistries.FEATURES);
    }

    private static <T> void remap(MissingMappingsEvent event, ResourceKey<? extends Registry<T>> key, IForgeRegistry<T> registry) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(key, MODID)) {
            ResourceLocation newId = new ResourceLocation(Animalia.MODID, mapping.getKey().getPath());
            if (registry.containsKey(newId)) {
                mapping.remap(registry.getValue(newId));
            }
        }
    }
}