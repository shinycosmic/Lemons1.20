package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaFoodTab {
    public static final List<RegistryObject<? extends Item>> FOOD_ITEMS = List.of(
            //Raws
            ModItems.RAW_ICEFISH,
            ModItems.RAW_FISH,
            ModItems.RAW_VENISON,

            //Cooked

            //Other|Breeding
            ModItems.AMPHIPOD,
            ModItems.GIGANTOCYPRIS,
            ModItems.ARTEMIA,
            ModItems.TADPOLE,
            ModItems.FISH_FOOD
    );

    public static void displayItems(CreativeModeTab.Output output) {
        FOOD_ITEMS.forEach(item -> output.accept(item.get()));
    }
}
