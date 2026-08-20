package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Animalia.MODID);

    //Foods
    public static final RegistryObject<Item> RAW_ICEFISH = ITEMS.register("raw_icefish", () -> new Item(new Item.Properties().food(ModFoods.RAW_ICEFISH)));
    public static final RegistryObject<Item> RAW_FISH = ITEMS.register("raw_fish", () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH)));
    public static final RegistryObject<Item> FISH_FOOD = ITEMS.register("fish_food", () -> new Item(new Item.Properties().food(ModFoods.FISH_FOOD)));
    public static final RegistryObject<Item> RAW_VENISON = ITEMS.register("raw_venison", () -> new Item(new Item.Properties().food(ModFoods.RAW_VENISON)));
    public static final RegistryObject<Item> COOKED_VENISON = ITEMS.register("cooked_venison", () -> new Item(new Item.Properties().food(ModFoods.COOKED_VENISON)));
    public static final RegistryObject<Item> COOKED_FISH = ITEMS.register("cooked_fish", () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH)));
    public static final RegistryObject<Item> RAW_CRUSTACEAN = ITEMS.register("raw_crustacean", () -> new Item(new Item.Properties().food(ModFoods.FISH_FOOD)));
    public static final RegistryObject<Item> COOKED_CRUSTACEAN = ITEMS.register("cooked_crustacean", () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH)));
    public static final RegistryObject<Item> BIVALVE_MEAT = ITEMS.register("bivalve_meat", () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH)));

    //Misc
    public static final RegistryObject<Item> FISH_EGG = ITEMS.register("fish_egg", () -> new FishEggItem(new Item.Properties()));
    public static final RegistryObject<Item> MOUND_FISH_EGG = ITEMS.register("mound_fish_egg", () -> new FishEggItem(new Item.Properties()));
    public static final RegistryObject<Item> BETTA_FISH_EGG = ITEMS.register("betta_fish_egg", () -> new BettaFishEggItem(new Item.Properties()));
    public static final RegistryObject<Item> HOLONET = ITEMS.register("holonet", () -> new HolonetItem(new Item.Properties().stacksTo(1)));

    //Animal Items
    public static final RegistryObject<Item> AMPHIPOD = ITEMS.register("amphipod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GIGANTOCYPRIS = ITEMS.register("gigantocypris", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARTEMIA = ITEMS.register("artemia", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TADPOLE = ITEMS.register("tadpole", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TERMITE = ITEMS.register("termite", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WORM = ITEMS.register("worm", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_CLARKII = ITEMS.register("procambarus_clarkii", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_ALLENI = ITEMS.register("procambarus_alleni", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_VIRGINALIS = ITEMS.register("procambarus_virginalis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_LUCIFUGUS = ITEMS.register("procambarus_lucifugus", () -> new Item(new Item.Properties()));

    //Helpers
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
