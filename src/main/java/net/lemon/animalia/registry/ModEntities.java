package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.*;
import net.lemon.animalia.entity.projectiles.WaterSpitProjectile;
import net.lemon.animalia.item.AnimaliaBucketItem;
import net.lemon.animalia.item.AnimaliaSpawnEggItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Animalia.MODID);
    public static final DeferredRegister<Item> SPAWN_EGGS = DeferredRegister.create(ForgeRegistries.ITEMS, Animalia.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, Animalia.MODID);
    public static final Map<String, RegistryObject<Item>> BUCKET_MAP= new HashMap<>();
    public static final Map<RegistryObject<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> ATTRIBUTE_SUPPLIERS = new HashMap<>();

    public static final RegistryObject<EntityType<ToothfishEntity>> DISSOSTICHUS_ELEGINOIDES = registerEntityType("dissostichus_eleginoides", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, ToothfishEntity::setAttributes, 0x4D5267, 0xA5A8B0, true);
    public static final RegistryObject<EntityType<ToothfishEntity>> ELEGINOPS_MACLOVINUS = registerEntityType("eleginops_maclovinus", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.9f, ToothfishEntity::setAttributes, 0x927A60, 0xFDEDD4, true);
    public static final RegistryObject<EntityType<CongolliEntity>> PSEUDAPHRITIS_URVILLII = registerEntityType("pseudaphritis_urvillii", CongolliEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, CongolliEntity::setAttributes, 0xceaf63, 0x384436, true);
    public static final RegistryObject<EntityType<ToothfishEntity>> PERCOPHIS_BRASILIENSIS = registerEntityType("percophis_brasiliensis", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, ToothfishEntity::setAttributes, 0x6e5540, 0xfff0cd, true);
    public static final RegistryObject<EntityType<PogonophryneEntity>> POGONOPHRYNE_MARMORATA = registerEntityType("pogonophryne_marmorata", PogonophryneEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, PogonophryneEntity::setAttributes, 0x8d7045, 0x54150c, true);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CHAENOCEPHALUS_ACERATUS = registerEntityType("chaenocephalus_aceratus", ChaenocephalusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, ChaenocephalusEntity::setAttributes, 0x403e1d, 0xcbccbb, true);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CYGNODRACO_MAWSONI = registerEntityType("cygnodraco_mawsoni", ChaenocephalusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, ChaenocephalusEntity::setAttributes, 0xb8c4e1, 0x6b4858, true);
    public static final RegistryObject<EntityType<SynbranchusEntity>> SYNBRANCHUS_MARMORATUS = registerEntityType("synbranchus_marmoratus", SynbranchusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.5f, SynbranchusEntity::setAttributes, 0xa39023, 0x2e2110, true);
    public static final RegistryObject<EntityType<SynbranchusEntity>> CHAUDHURIA_CAUDATA = registerEntityType("chaudhuria_caudata", SynbranchusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.5f, SynbranchusEntity::setAttributes, 0x977757, 0x9f604e, true);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ARMATUS = registerEntityType("mastacembelus_armatus", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.7f, 0.3f, MastacembelusEntity::setAttributes, 0xbd8741, 0x4c392b, true);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ERYTHROTAENIA = registerEntityType("mastacembelus_erythrotaenia", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.7f, 0.3f, MastacembelusEntity::setAttributes, 0x394046, 0xb7241d, true);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MACROGNATHUS_SIAMENSIS = registerEntityType("macrognathus_siamensis", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes, 0x483e34, 0xdcc96c, true);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_BRICHARDI = registerEntityType("mastacembelus_brichardi", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes, 0xe6c2e8, 0xf5f0ff, true);
    public static final RegistryObject<EntityType<MastacembelusEntity>> SINOBDELLA_SINENSIS = registerEntityType("sinobdella_sinensis", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes, 0xf2d5af, 0x614a3c, true);
    public static final RegistryObject<EntityType<RakthamichthysEntity>> RAKTHAMICHTHYS_INDICUS = registerEntityType("rakthamichthys_indicus", RakthamichthysEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.2f, RakthamichthysEntity::setAttributes, 0xab2f1e, 0xea7f49, true);
    public static final RegistryObject<EntityType<IndostomusEntity>> INDOSTOMUS_PARADOXUS = registerEntityType("indostomus_paradoxus", IndostomusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.2f, IndostomusEntity::setAttributes, 0x744926, 0xf8efd0, true);
    public static final RegistryObject<EntityType<RoosterfishEntity>> NEMATISTIUS_PECTORALIS = registerEntityType("nematistius_pectoralis", RoosterfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.8f, 0.6f, RoosterfishEntity::setAttributes, 0x7d8a90, 0x282629, true);
    public static final RegistryObject<EntityType<ToxotesEntity>> TOXOTES_CHATAREUS = registerEntityType("toxotes_chatareus", ToxotesEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.6f, ToxotesEntity::setAttributes, 0xd6d6d6, 0x111110, true);
    public static final RegistryObject<EntityType<BettaEntity>> BETTA_SPLENDENS = registerEntityType("betta_splendens", BettaEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.3f, BettaEntity::setAttributes, 0xF12D03, 0x608EE9, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> SCATOPHAGUS_ARGUS = registerEntityType("scatophagus_argus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes, 0xbdc682, 0x1c201b, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> POMACANTHUS_IMPERATOR = registerEntityType("pomacanthus_imperator", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes, 0x1f56f7, 0xf5f74b, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> NASO_BREVIROSTRIS = registerEntityType("naso_brevirostris", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes, 0x718e91, 0x8b9aa0, true);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> PARACANTHURUS_HEPATUS = registerEntityType("paracanthurus_hepatus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, GrazeSchoolingEntity::setAttributes, 0x3b69ff, 0x030208, true);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZANCLUS_CORNUTUS = registerEntityType("zanclus_cornutus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes, 0x151b1b, 0xf6fafa, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> CHELMON_ROSTRATUS = registerEntityType("chelmon_rostratus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes, 0xee872f, 0xd8e1e8, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> CHAETODON_AURIGA = registerEntityType("chaetodon_auriga", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes, 0x393424, 0xffeb64, true);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> SIGANUS_VULPINUS = registerEntityType("siganus_vulpinus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes, 0xf4e609, 0x0c0805, true);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZEBRASOMA_VELIFER = registerEntityType("zebrasoma_velifer", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.6f, GrazeSchoolingEntity::setAttributes, 0x4d3d25, 0xb68e0b, true);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZEBRASOMA_FLAVESCENS = registerEntityType("zebrasoma_flavescens", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes, 0xdabd04, 0xfffbb4, true);
    public static final RegistryObject<EntityType<CavefishEntity>> GITCHAK_NAKANA = registerEntityType("gitchak_nakana", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xc55522, 0xe71f11, true);
    public static final RegistryObject<EntityType<CavefishEntity>> CYPRINODON_DIABOLIS = registerEntityType("cyprinodon_diabolis", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0x322f6d, 0x8599cd, true);
    public static final RegistryObject<EntityType<CavefishEntity>> AMBLYOPSIS_HOOSIERI = registerEntityType("amblyopsis_hoosieri", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xedcebc, 0xe6977a, true);
    public static final RegistryObject<EntityType<CavefishEntity>> KRYPTOGLANIS_SHAJII = registerEntityType("kryptoglanis_shajii", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xdb729d, 0xfcedf1, true);
    public static final RegistryObject<EntityType<CavefishEntity>> SINOCYCLOCHEILUS_LONGICORNUS = registerEntityType("sinocyclocheilus_longicornus", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xe4c08c, 0xc16b54, true);
    public static final RegistryObject<EntityType<CavefishEntity>> SINOCYCLOCHEILUS_ANATIROSTRIS = registerEntityType("sinocyclocheilus_anatirostris", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xecbca4, 0xa25547, true);
    public static final RegistryObject<EntityType<CavefishEntity>> SINOCYCLOCHEILUS_HYALINUS = registerEntityType("sinocyclocheilus_hyalinus", CavefishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, CavefishEntity::setAttributes, 0xf6edd3, 0xd4c599, true);

    public static final RegistryObject<EntityType<PangasianodonEntity>> PANGASIANODON_GIGAS = registerEntityType("pangasianodon_gigas", PangasianodonEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, PangasianodonEntity::setAttributes, 0x384643, 0xb4ccc0, true);
    public static final RegistryObject<EntityType<HydrocynusEntity>> HYDROCYNUS_GOLIATH = registerEntityType("hydrocynus_goliath", HydrocynusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, HydrocynusEntity::setAttributes, 0xb7b075, 0xc83226, true);

    //Non Fish
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_CLARKII = registerEntityType("procambarus_clarkii", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes, 0x932a2a, 0x202522, true);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_ALLENI = registerEntityType("procambarus_alleni", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes, 0xdb743a, 0x26a5c6, true);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_VIRGINALIS = registerEntityType("procambarus_virginalis", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes, 0x332b20, 0x8a7346, true);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_LUCIFUGUS = registerEntityType("procambarus_lucifugus", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes, 0xab9782, 0xbbb8c9, true);
    //Actual Land animals
    public static final RegistryObject<EntityType<PangolinEntity>> SMUTSIA_GIGANTEA = registerEntityType("smutsia_gigantea", PangolinEntity::new, ModMobCategories.ANIMALIA_LAND, 0.9f, 0.9f, PangolinEntity::setAttributes, 0x8e6b3c, 0xffc06a);

    public static final RegistryObject<EntityType<WaterSpitProjectile>> WATER_SPIT = ENTITY_TYPES.register("water_spit",
            () -> EntityType.Builder.<WaterSpitProjectile>of(WaterSpitProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("water_spit"));

    //Supplier Helper Methods
    public static <T extends Mob> RegistryObject<EntityType<T>> registerEntityType
    (String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, Supplier<AttributeSupplier> attributes, int eggBc, int eggHc) {
        RegistryObject<EntityType<T>> type = ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
        ATTRIBUTE_SUPPLIERS.put(type, attributes);
        SPAWN_EGGS.register(name + "_spawn_egg", () -> new AnimaliaSpawnEggItem(type, eggBc, eggHc, new Item.Properties()));
        return type;
    }

    public static <T extends Mob> RegistryObject<EntityType<T>> registerEntityType
            (String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height, Supplier<AttributeSupplier> attributes, int eggBc, int eggHc, boolean bucketable) {
        RegistryObject<EntityType<T>> type = registerEntityType(name, factory, category, width, height, attributes, eggBc, eggHc);
        if(bucketable) {
            RegistryObject<Item> bucket = registerBucket(name + "_bucket", type);
            BUCKET_MAP.put(name, bucket);
        }
        return type;
    }

    //These should not need to be touched
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        SPAWN_EGGS.register(eventBus);
        BUCKETS.register(eventBus);
    }

    public static RegistryObject<Item> registerBucket(String name, Supplier<? extends EntityType<?>> type) {
        return BUCKETS.register(name, () -> new AnimaliaBucketItem(type, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    }


}
