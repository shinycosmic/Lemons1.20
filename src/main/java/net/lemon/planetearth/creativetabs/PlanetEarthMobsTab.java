package net.lemon.planetearth.creativetabs;

import net.lemon.planetearth.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class PlanetEarthMobsTab {
    public static final List<RegistryObject<? extends Item>> SPAWN_EGGS = List.of(
            //Spawn Eggs
            ModItems.OCELLATED_PAMPAS_SNAKE_SPAWN_EGG

    );

    public static void displayItems(CreativeModeTab.Output output) {
        SPAWN_EGGS.forEach(item -> output.accept(item.get()));
    }
}
