package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.item.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
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
    public static final RegistryObject<Item> BIVALVE_MEAT = ITEMS.register("bivalve_meat", () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH)));



    //Buckets
    public static final RegistryObject<Item> DISSOSTICHUS_ELEGINOIDES_BUCKET = registerBucket("dissostichus_eleginoides_bucket", ModEntities.DISSOSTICHUS_ELEGINOIDES);
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
    public static final RegistryObject<Item> INDOSTOMUS_PARADOXUS_BUCKET = registerBucket("indostomus_paradoxus_bucket", ModEntities.INDOSTOMUS_PARADOXUS);
    public static final RegistryObject<Item> NEMATISTIUS_PECTORALIS_BUCKET = registerBucket("nematistius_pectoralis_bucket", ModEntities.NEMATISTIUS_PECTORALIS);
    public static final RegistryObject<Item> TOXOTES_CHATAREUS_BUCKET = registerBucket("toxotes_chatareus_bucket", ModEntities.TOXOTES_CHATAREUS);
//    public static final RegistryObject<Item> LEPTOBRAMA_MUELLERI_BUCKET = registerBucket("leptobrama_muelleri_bucket", ModEntities.LEPTOBRAMA_MUELLERI);
    public static final RegistryObject<Item> PANGASIANODON_GIGAS_BUCKET = registerBucket("pangasianodon_gigas_bucket", ModEntities.PANGASIANODON_GIGAS);
    public static final RegistryObject<Item> SCATOPHAGUS_ARGUS_BUCKET = registerBucket("scatophagus_argus_bucket", ModEntities.SCATOPHAGUS_ARGUS);
    public static final RegistryObject<Item> POMACANTHUS_IMPERATOR_BUCKET = registerBucket("pomacanthus_imperator_bucket", ModEntities.POMACANTHUS_IMPERATOR);
    public static final RegistryObject<Item> NASO_BREVIROSTRIS_BUCKET = registerBucket("naso_brevirostris_bucket", ModEntities.NASO_BREVIROSTRIS);
    public static final RegistryObject<Item> ZANCLUS_CORNUTUS_BUCKET = registerBucket("zanclus_cornutus_bucket", ModEntities.ZANCLUS_CORNUTUS);
    public static final RegistryObject<Item> PARACANTHURUS_HEPATUS_BUCKET = registerBucket("paracanthurus_hepatus_bucket", ModEntities.PARACANTHURUS_HEPATUS);
    public static final RegistryObject<Item> CHELMON_ROSTRATUS_BUCKET = registerBucket("chelmon_rostratus_bucket", ModEntities.CHELMON_ROSTRATUS);
    public static final RegistryObject<Item> CHAETODON_AURIGA_BUCKET = registerBucket("chaetodon_auriga_bucket", ModEntities.CHAETODON_AURIGA);
    public static final RegistryObject<Item> SIGANUS_VULPINUS_BUCKET = registerBucket("siganus_vulpinus_bucket", ModEntities.SIGANUS_VULPINUS);
    public static final RegistryObject<Item> ZEBRASOMA_FLAVESCENS_BUCKET = registerBucket("zebrasoma_flavescens_bucket", ModEntities.ZEBRASOMA_FLAVESCENS);
    public static final RegistryObject<Item> ZEBRASOMA_VELIFER_BUCKET = registerBucket("zebrasoma_velifer_bucket", ModEntities.ZEBRASOMA_VELIFER);
    public static final RegistryObject<Item> HYDROCYNUS_GOLIATH_BUCKET = registerBucket("hydrocynus_goliath_bucket", ModEntities.HYDROCYNUS_GOLIATH);
    public static final RegistryObject<Item> SINOCYCLOCHEILUS_ANATIROSTRIS_BUCKET = registerBucket("sinocyclocheilus_anatirostris_bucket", ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS);
    public static final RegistryObject<Item> SINOCYCLOCHEILUS_HYALINUS_BUCKET = registerBucket("sinocyclocheilus_hyalinus_bucket", ModEntities.SINOCYCLOCHEILUS_HYALINUS);
    public static final RegistryObject<Item> SINOCYCLOCHEILUS_LONGICORNUS_BUCKET = registerBucket("sinocyclocheilus_longicornus_bucket", ModEntities.SINOCYCLOCHEILUS_LONGICORNUS);
    public static final RegistryObject<Item> GITCHAK_NAKANA_BUCKET = registerBucket("gitchak_nakana_bucket", ModEntities.GITCHAK_NAKANA);
    public static final RegistryObject<Item> KRYPTOGLANIS_SHAJII_BUCKET = registerBucket("kryptoglanis_shajii_bucket", ModEntities.KRYPTOGLANIS_SHAJII);
    public static final RegistryObject<Item> AMBLYOPSIS_HOOSIERI_BUCKET = registerBucket("amblyopsis_hoosieri_bucket", ModEntities.AMBLYOPSIS_HOOSIERI);
    public static final RegistryObject<Item> CYPRINODON_DIABOLIS_BUCKET = registerBucket("cyprinodon_diabolis_bucket", ModEntities.CYPRINODON_DIABOLIS);

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
    public static final RegistryObject<Item> WORM = ITEMS.register("worm", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_CLARKII = ITEMS.register("procambarus_clarkii", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_ALLENI = ITEMS.register("procambarus_alleni", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_VIRGINALIS = ITEMS.register("procambarus_virginalis", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCAMBARUS_LUCIFUGUS = ITEMS.register("procambarus_lucifugus", () -> new Item(new Item.Properties()));

    //Supplier Helper Methods
    public static RegistryObject<Item> registerBucket(String name, Supplier<? extends EntityType<?>> type) {
        return ITEMS.register(name, () -> new AnimaliaBucketItem(type, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
