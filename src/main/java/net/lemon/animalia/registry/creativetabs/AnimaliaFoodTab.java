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
            ModItems.COOKED_FISH,
            ModItems.RAW_VENISON,
            ModItems.COOKED_VENISON,
            ModItems.RAW_CRUSTACEAN,
            ModItems.COOKED_CRUSTACEAN,
            ModItems.BIVALVE_MEAT,
            ModItems.RAW_SHARK,
            ModItems.COOKED_SHARK,
            ModItems.RAW_SMALL_MAMMAL,
            ModItems.COOKED_SMALL_MAMMAL,
            ModItems.RAW_LARGE_MAMMAL,
            ModItems.COOKED_LARGE_MAMMAL,

            //Other|Breeding
            ModItems.AMPHIPOD,
            ModItems.GIGANTOCYPRIS,
            ModItems.ARTEMIA,
            ModItems.TADPOLE,
            ModItems.FISH_FOOD,
            ModItems.TERMITE,
            ModItems.WORM,
            ModItems.GRUB,
            ModItems.PROCAMBARUS_CLARKII,
            ModItems.PROCAMBARUS_ALLENI,
            ModItems.PROCAMBARUS_VIRGINALIS,
            ModItems.PROCAMBARUS_LUCIFUGUS
    );

    public static void displayItems(CreativeModeTab.Output output) {
        FOOD_ITEMS.forEach(item -> output.accept(item.get()));
    }
}
