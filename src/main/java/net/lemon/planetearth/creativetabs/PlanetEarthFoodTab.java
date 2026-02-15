package net.lemon.planetearth.creativetabs;

import net.lemon.planetearth.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class PlanetEarthFoodTab {
    public static final List<RegistryObject<? extends Item>> FOOD_ITEMS = List.of(
            //Raws
            ModItems.FISH_FILLET,
            ModItems.RAW_RODENT

            //Cooked
    );

    public static void displayItems(CreativeModeTab.Output output) {
        FOOD_ITEMS.forEach(item -> output.accept(item.get()));
    }
}
