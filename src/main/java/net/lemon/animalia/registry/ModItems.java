package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.item.AnimaliaBucketItem;
import net.lemon.animalia.item.BettaFishEggItem;
import net.lemon.animalia.item.FishEggItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Animalia.MODID);

    /// TODO USE WHAT NATURALISTMOD HAS FOR REGISTRIES
    //Foods
    public static final RegistryObject<Item> RAW_ICEFISH = ITEMS.register("raw_icefish", () -> new Item(new Item.Properties().food(ModFoods.RAW_ICEFISH)));
    public static final RegistryObject<Item> RAW_FISH = ITEMS.register("raw_fish", () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH)));

    //Buckets
    public static final RegistryObject<Item> CHILEANSEABASS_BUCKET = registerBucket("dissostichus_eleginoides_bucket", ModEntities.CHILEANSEABASS);
    public static final RegistryObject<Item> ELEGINOPS_MACLOVINUS_BUCKET = registerBucket("eleginops_maclovinus_bucket", ModEntities.ELEGINOPS_MACLOVINUS);
    public static final RegistryObject<Item> PSEUDAPHRITIS_URVILLII_BUCKET = registerBucket("pseudaphritis_urvillii_bucket", ModEntities.PSEUDAPHRITIS_URVILLII);
    public static final RegistryObject<Item> BETTA_SPLENDENS_BUCKET = registerBucket("betta_splendens_bucket", ModEntities.BETTA_SPLENDENS);



    //Misc
    public static final RegistryObject<Item> FISH_EGG = ITEMS.register("fish_egg", () -> new FishEggItem(new Item.Properties()));
    public static final RegistryObject<Item> BETTA_FISH_EGG = ITEMS.register("betta_fish_egg", () -> new BettaFishEggItem(new Item.Properties()));

    //Animal Items
    public static final RegistryObject<Item> AMPHIPOD = ITEMS.register("amphipod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GIGANTOCYPRIS = ITEMS.register("gigantocypris", () -> new Item(new Item.Properties()));

    //Spawn Eggs
    public static final RegistryObject<Item> CHILEANSEABASS_SPAWN_EGG = registerSpawnEgg("dissostichus_eleginoides_spawn_egg", ModEntities.CHILEANSEABASS, 0x4D5267, 0xA5A8B0);
    public static final RegistryObject<Item> ELEGINOPS_MACLOVINUS_SPAWN_EGG = registerSpawnEgg("eleginops_maclovinus_spawn_egg", ModEntities.ELEGINOPS_MACLOVINUS, 0x927A60, 0xFDEDD4);
    public static final RegistryObject<Item> PSEUDAPHRITIS_URVILLII_SPAWN_EGG = registerSpawnEgg("pseudaphritis_urvillii_spawn_egg", ModEntities.PSEUDAPHRITIS_URVILLII, 0xceaf63, 0x384436);
    public static final RegistryObject<Item> BETTA_SPLENDENS_SPAWN_EGG = registerSpawnEgg("betta_splendens_spawn_egg", ModEntities.BETTA_SPLENDENS, 0xF12D03, 0x608EE9);

    //Supplier Helper Methods
    public static <T extends Mob> RegistryObject<Item> registerSpawnEgg(String name, Supplier<EntityType<T>> type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
    public static RegistryObject<Item> registerBucket(String name, Supplier<? extends EntityType<?>> type) {
        return ITEMS.register(name, () -> new AnimaliaBucketItem(type, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
