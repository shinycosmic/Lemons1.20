package net.lemon.planetearth.item;

import net.lemon.planetearth.PlanetEarth;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PlanetEarth.MODID);

    //Items
    public static final RegistryObject<Item> FISH_FILLET = ITEMS.register("fish_fillet",
            () -> new Item(new Item.Properties().food(ModFoods.FISH_FILLET)));

    public static final RegistryObject<Item> RAW_RODENT = ITEMS.register("raw_rodent",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_RODENT)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
