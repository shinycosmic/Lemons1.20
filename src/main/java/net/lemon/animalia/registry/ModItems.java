package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Animalia.MODID);

    /// TODO USE WHAT NATURALISTMOD HAS FOR REGISTRIES
    //Items
    public static final RegistryObject<Item> RAW_ICEFISH = ITEMS.register("raw_icefish",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_ICEFISH)));

//    public static final RegistryObject<Item> RAW_RODENT = ITEMS.register("raw_rodent",
//            () -> new Item(new Item.Properties().food(ModFoods.RAW_RODENT)));

    //Spawn Eggs
//    public static final RegistryObject<Item> OCELLATED_PAMPAS_SNAKE_SPAWN_EGG = ITEMS.register("ocellatedpampassnake_spawn_egg",
//            () -> new ForgeSpawnEggItem(ModEntities.OCELLATED_PAMPAS_SNAKE, 0xA5A8B0, 0x4D5267, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
