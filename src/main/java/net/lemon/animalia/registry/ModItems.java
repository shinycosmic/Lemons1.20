package net.lemon.animalia.registry;

import com.google.errorprone.annotations.Var;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.item.*;
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

    //Foods
    public static final RegistryObject<Item> RAW_ICEFISH = ITEMS.register("raw_icefish", () -> new Item(new Item.Properties().food(ModFoods.RAW_ICEFISH)));
    public static final RegistryObject<Item> RAW_FISH = ITEMS.register("raw_fish", () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH)));
    public static final RegistryObject<Item> FISH_FOOD = ITEMS.register("fish_food", () -> new Item(new Item.Properties().food(ModFoods.FISH_FOOD)));
    public static final RegistryObject<Item> RAW_VENISON = ITEMS.register("raw_venison", () -> new Item(new Item.Properties().food(ModFoods.RAW_VENISON)));
    public static final RegistryObject<Item> COOKED_VENISON = ITEMS.register("cooked_venison", () -> new Item(new Item.Properties().food(ModFoods.COOKED_VENISON)));
    public static final RegistryObject<Item> COOKED_FISH = ITEMS.register("cooked_fish", () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH)));
    public static final RegistryObject<Item> RAW_CRUSTACEAN = ITEMS.register("raw_crustacean", () -> new Item(new Item.Properties().food(ModFoods.FISH_FOOD)));
    public static final RegistryObject<Item> COOKED_CRUSTACEAN = ITEMS.register("cooked_crustacean", () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH)));



    //Buckets
    public static final RegistryObject<Item> CHILEANSEABASS_BUCKET = registerBucket("dissostichus_eleginoides_bucket", ModEntities.CHILEANSEABASS);
    public static final RegistryObject<Item> ELEGINOPS_MACLOVINUS_BUCKET = registerBucket("eleginops_maclovinus_bucket", ModEntities.ELEGINOPS_MACLOVINUS);
    public static final RegistryObject<Item> PSEUDAPHRITIS_URVILLII_BUCKET = registerBucket("pseudaphritis_urvillii_bucket", ModEntities.PSEUDAPHRITIS_URVILLII);
    public static final RegistryObject<Item> PERCOPHIS_BRASILIENSIS_BUCKET = registerBucket("percophis_brasiliensis_bucket", ModEntities.PERCOPHIS_BRASILIENSIS);
//    public static final RegistryObject<Item> BOVICHTUS_VARIEGATUS_BUCKET = registerBucket("bovichtus_variegatus_bucket", ModEntities.BOVICHTUS_VARIEGATUS);
    public static final RegistryObject<Item> POGONOPHRYNE_MARMORATA_BUCKET = registerBucket("pogonophryne_marmorata_bucket", ModEntities.POGONOPHRYNE_MARMORATA);
    public static final RegistryObject<Item> CYGNODRACO_MAWSONI_BUCKET = registerBucket("cygnodraco_mawsoni_bucket", ModEntities.CYGNODRACO_MAWSONI);
    public static final RegistryObject<Item> CHAENOCEPHALUS_ACERATUS_BUCKET = registerBucket("chaenocephalus_aceratus_bucket", ModEntities.CHAENOCEPHALUS_ACERATUS);
    public static final RegistryObject<Item> SYNBRANCHUS_MARMORATUS_BUCKET = registerBucket("synbranchus_marmoratus_bucket", ModEntities.SYNBRANCHUS_MARMORATUS);
    public static final RegistryObject<Item> CHAUDHURIA_CAUDATA_BUCKET = registerBucket("chaudhuria_caudata_bucket", ModEntities.CHAUDHURIA_CAUDATA);
    public static final RegistryObject<Item> MASTACEMBELUS_ARMATUS_BUCKET = registerBucket("mastacembelus_armatus_bucket", ModEntities.MASTACEMBELUS_ARMATUS);
    public static final RegistryObject<Item> MASTACEMBELUS_ERYTHROTAENIA_BUCKET = registerBucket("mastacembelus_erythrotaenia_bucket", ModEntities.MASTACEMBELUS_ERYTHROTAENIA);
    public static final RegistryObject<Item> MACROGNATHUS_SIAMENSIS_BUCKET = registerBucket("macrognathus_siamensis_bucket", ModEntities.MACROGNATHUS_SIAMENSIS);
    public static final RegistryObject<Item> MASTACEMBELUS_BRICHARDI_BUCKET = registerBucket("mastacembelus_brichardi_bucket", ModEntities.MASTACEMBELUS_BRICHARDI);
    public static final RegistryObject<Item> SINOBDELLA_SINENSIS_BUCKET = registerBucket("sinobdella_sinensis_bucket", ModEntities.SINOBDELLA_SINENSIS);
    public static final RegistryObject<Item> RAKTHAMICHTHYS_INDICUS_BUCKET = registerBucket("rakthamichthys_indicus_bucket", ModEntities.RAKTHAMICHTHYS_INDICUS);
    public static final RegistryObject<Item> NEMATISTIUS_PECTORALIS_BUCKET = registerBucket("nematistius_pectoralis_bucket", ModEntities.NEMATISTIUS_PECTORALIS);
    public static final RegistryObject<Item> TOXOTES_CHATAREUS_BUCKET = registerBucket("toxotes_chatareus_bucket", ModEntities.TOXOTES_CHATAREUS);
//    public static final RegistryObject<Item> LEPTOBRAMA_MUELLERI_BUCKET = registerBucket("leptobrama_muelleri_bucket", ModEntities.LEPTOBRAMA_MUELLERI);
//    public static final RegistryObject<Item> PANGASIANODON_GIGAS_BUCKET = registerBucket("pangasianodon_gigas_bucket", ModEntities.PANGASIANODON_GIGAS);
    public static final RegistryObject<Item> SCATOPHAGUS_ARGUS_BUCKET = registerBucket("scatophagus_argus_bucket", ModEntities.SCATOPHAGUS_ARGUS);
    public static final RegistryObject<Item> BETTA_SPLENDENS_BUCKET = registerBucket("betta_splendens_bucket", ModEntities.BETTA_SPLENDENS);
    public static final RegistryObject<Item> PROCAMBARUS_CLARKII_BUCKET = registerBucket("procambarus_clarkii_bucket", ModEntities.PROCAMBARUS_CLARKII);
    public static final RegistryObject<Item> PROCAMBARUS_ALLENI_BUCKET = registerBucket("procambarus_alleni_bucket", ModEntities.PROCAMBARUS_ALLENI);
    public static final RegistryObject<Item> PROCAMBARUS_VIRGINALIS_BUCKET = registerBucket("procambarus_virginalis_bucket", ModEntities.PROCAMBARUS_VIRGINALIS);
    public static final RegistryObject<Item> PROCAMBARUS_LUCIFUGUS_BUCKET = registerBucket("procambarus_lucifugus_bucket", ModEntities.PROCAMBARUS_LUCIFUGUS);




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
    public static final RegistryObject<Item> PROCAMBARUS_CLARKII = ITEMS.register("procambarus_clarkii", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_ALLENI = ITEMS.register("procambarus_alleni", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_VIRGINALIS = ITEMS.register("procambarus_virginalis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_LUCIFUGUS = ITEMS.register("procambarus_lucifugus", () -> new Item(new Item.Properties()));

    //Spawn Eggs
    public static final RegistryObject<Item> CHILEANSEABASS_SPAWN_EGG = registerSpawnEgg("dissostichus_eleginoides_spawn_egg", ModEntities.CHILEANSEABASS, 0x4D5267, 0xA5A8B0);
    public static final RegistryObject<Item> ELEGINOPS_MACLOVINUS_SPAWN_EGG = registerSpawnEgg("eleginops_maclovinus_spawn_egg", ModEntities.ELEGINOPS_MACLOVINUS, 0x927A60, 0xFDEDD4);
    public static final RegistryObject<Item> PSEUDAPHRITIS_URVILLII_SPAWN_EGG = registerSpawnEgg("pseudaphritis_urvillii_spawn_egg", ModEntities.PSEUDAPHRITIS_URVILLII, 0xceaf63, 0x384436);
    public static final RegistryObject<Item> PERCOPHIS_BRASILIENSIS_SPAWN_EGG = registerSpawnEgg("percophis_brasiliensis_spawn_egg", ModEntities.PERCOPHIS_BRASILIENSIS, 0x6e5540, 0xfff0cd);
//    public static final RegistryObject<Item> BOVICHTUS_VARIEGATUS_SPAWN_EGG = registerSpawnEgg("bovichtus_variegatus_spawn_egg", ModEntities.BOVICHTUS_VARIEGATUS, 0x6e5540, 0xfff0cd);
    public static final RegistryObject<Item> POGONOPHRYNE_MARMORATA_SPAWN_EGG = registerSpawnEgg("pogonophryne_marmorata_spawn_egg", ModEntities.POGONOPHRYNE_MARMORATA, 0x8d7045, 0x54150c);
    public static final RegistryObject<Item> CYGNODRACO_MAWSONI_SPAWN_EGG = registerSpawnEgg("cygnodraco_mawsoni_spawn_egg", ModEntities.CYGNODRACO_MAWSONI, 0xb8c4e1, 0x6b4858);
    public static final RegistryObject<Item> CHAENOCEPHALUS_ACERATUS_SPAWN_EGG = registerSpawnEgg("chaenocephalus_aceratus_spawn_egg", ModEntities.CHAENOCEPHALUS_ACERATUS, 0x403e1d, 0xcbccbb);
    public static final RegistryObject<Item> SYNBRANCHUS_MARMORATUS_SPAWN_EGG = registerSpawnEgg("synbranchus_marmoratus_spawn_egg", ModEntities.SYNBRANCHUS_MARMORATUS, 0xa39023, 0x2e2110);
    public static final RegistryObject<Item> CHAUDHURIA_CAUDATA_SPAWN_EGG = registerSpawnEgg("chaudhuria_caudata_spawn_egg", ModEntities.CHAUDHURIA_CAUDATA, 0x977757, 0x9f604e);
    public static final RegistryObject<Item> MASTACEMBELUS_ARMATUS_SPAWN_EGG = registerSpawnEgg("mastacembelus_armatus_spawn_egg", ModEntities.MASTACEMBELUS_ARMATUS, 0xbd8741, 0x4c392b);
    public static final RegistryObject<Item> MASTACEMBELUS_ERYTHROTAENIA_SPAWN_EGG = registerSpawnEgg("mastacembelus_erythrotaenia_spawn_egg", ModEntities.MASTACEMBELUS_ERYTHROTAENIA, 0x394046, 0xb7241d);
    public static final RegistryObject<Item> MACROGNATHUS_SIAMENSIS_SPAWN_EGG = registerSpawnEgg("macrognathus_siamensis_spawn_egg", ModEntities.MACROGNATHUS_SIAMENSIS, 0x483e34, 0xdcc96c);
    public static final RegistryObject<Item> MASTACEMBELUS_BRICHARDI_SPAWN_EGG = registerSpawnEgg("mastacembelus_brichardi_spawn_egg", ModEntities.MASTACEMBELUS_BRICHARDI, 0xe6c2e8, 0xf5f0ff);
    public static final RegistryObject<Item> SINOBDELLA_SINENSIS_SPAWN_EGG = registerSpawnEgg("sinobdella_sinensis_spawn_egg", ModEntities.SINOBDELLA_SINENSIS, 0xf2d5af, 0x614a3c);
    public static final RegistryObject<Item> RAKTHAMICHTHYS_INDICUS_SPAWN_EGG = registerSpawnEgg("rakthamichthys_indicus_spawn_egg", ModEntities.RAKTHAMICHTHYS_INDICUS, 0xab2f1e, 0xea7f49);
    public static final RegistryObject<Item> NEMATISTIUS_PECTORALIS_SPAWN_EGG = registerSpawnEgg("nematistius_pectoralis_spawn_egg", ModEntities.NEMATISTIUS_PECTORALIS, 0x7d8a90, 0x282629);
    public static final RegistryObject<Item> TOXOTES_CHATAREUS_SPAWN_EGG = registerSpawnEgg("toxotes_chatareus_spawn_egg", ModEntities.TOXOTES_CHATAREUS, 0xd6d6d6, 0x111110);
//    public static final RegistryObject<Item> LEPTOBRAMA_MUELLERI_SPAWN_EGG = registerSpawnEgg("leptobrama_muelleri_spawn_egg", ModEntities.LEPTOBRAMA_MUELLERI, 0x7d8a90, 0x282629);
//    public static final RegistryObject<Item> PANGASIANODON_GIGAS_SPAWN_EGG = registerSpawnEgg("pangasianodon_gigas_spawn_egg", ModEntities.PANGASIANODON_GIGAS, 0x7d8a90, 0x282629);
    public static final RegistryObject<Item> SCATOPHAGUS_ARGUS_SPAWN_EGG = registerSpawnEgg("scatophagus_argus_spawn_egg", ModEntities.SCATOPHAGUS_ARGUS, 0xe0d738, 0x1c201b);

    //Non Fish Spawn Eggs
    public static final RegistryObject<Item> PROCAMBARUS_CLARKII_SPAWN_EGG = registerSpawnEgg("procambarus_clarkii_spawn_egg", ModEntities.PROCAMBARUS_CLARKII, 0x932a2a, 0x202522);
    public static final RegistryObject<Item> PROCAMBARUS_ALLENI_SPAWN_EGG = registerSpawnEgg("procambarus_alleni_spawn_egg", ModEntities.PROCAMBARUS_ALLENI, 0xdb743a, 0x26a5c6);
    public static final RegistryObject<Item> PROCAMBARUS_VIRGINALIS_SPAWN_EGG = registerSpawnEgg("procambarus_virginalis_spawn_egg", ModEntities.PROCAMBARUS_VIRGINALIS, 0x332b20, 0x8a7346);
    public static final RegistryObject<Item> PROCAMBARUS_LUCIFUGUS_SPAWN_EGG = registerSpawnEgg("procambarus_lucifugus_spawn_egg", ModEntities.PROCAMBARUS_LUCIFUGUS, 0xab9782, 0xbbb8c9);



    public static final RegistryObject<Item> BETTA_SPLENDENS_SPAWN_EGG = registerSpecialSpawnEgg("betta_splendens_spawn_egg", ModEntities.BETTA_SPLENDENS, 0xF12D03, 0x608EE9);




    //Supplier Helper Methods
    public static <T extends Mob> RegistryObject<Item> registerSpawnEgg(String name, Supplier<EntityType<T>> type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new AnimaliaSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
    public static <T extends Mob> RegistryObject<Item> registerSpecialSpawnEgg(String name, Supplier<EntityType<T>> type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new VariantizedSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
    public static RegistryObject<Item> registerBucket(String name, Supplier<? extends EntityType<?>> type) {
        return ITEMS.register(name, () -> new AnimaliaBucketItem(type, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
