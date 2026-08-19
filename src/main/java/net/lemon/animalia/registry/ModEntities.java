package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.*;
import net.lemon.animalia.entity.projectiles.WaterSpitProjectile;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Animalia.MODID);
    public static final Map<RegistryObject<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier>> ATTRIBUTE_SUPPLIERS = new HashMap<>();

    public static final RegistryObject<EntityType<ToothfishEntity>> DISSOSTICHUS_ELEGINOIDES = registerEntityType("dissostichus_eleginoides", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, ToothfishEntity::setAttributes);
    public static final RegistryObject<EntityType<ToothfishEntity>> ELEGINOPS_MACLOVINUS = registerEntityType("eleginops_maclovinus", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.9f, ToothfishEntity::setAttributes);
    public static final RegistryObject<EntityType<CongolliEntity>> PSEUDAPHRITIS_URVILLII = registerEntityType("pseudaphritis_urvillii", CongolliEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, CongolliEntity::setAttributes);
    public static final RegistryObject<EntityType<ToothfishEntity>> PERCOPHIS_BRASILIENSIS = registerEntityType("percophis_brasiliensis", ToothfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.5f, ToothfishEntity::setAttributes);
    public static final RegistryObject<EntityType<PogonophryneEntity>> POGONOPHRYNE_MARMORATA = registerEntityType("pogonophryne_marmorata", PogonophryneEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, PogonophryneEntity::setAttributes);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CHAENOCEPHALUS_ACERATUS = registerEntityType("chaenocephalus_aceratus", ChaenocephalusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, ChaenocephalusEntity::setAttributes);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CYGNODRACO_MAWSONI = registerEntityType("cygnodraco_mawsoni", ChaenocephalusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, ChaenocephalusEntity::setAttributes);
    public static final RegistryObject<EntityType<SynbranchusEntity>> SYNBRANCHUS_MARMORATUS = registerEntityType("synbranchus_marmoratus", SynbranchusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.5f, SynbranchusEntity::setAttributes);
    public static final RegistryObject<EntityType<SynbranchusEntity>> CHAUDHURIA_CAUDATA = registerEntityType("chaudhuria_caudata", SynbranchusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.5f, SynbranchusEntity::setAttributes);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ARMATUS = registerEntityType("mastacembelus_armatus", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.7f, 0.3f, MastacembelusEntity::setAttributes);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ERYTHROTAENIA = registerEntityType("mastacembelus_erythrotaenia", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.7f, 0.3f, MastacembelusEntity::setAttributes);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MACROGNATHUS_SIAMENSIS = registerEntityType("macrognathus_siamensis", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_BRICHARDI = registerEntityType("mastacembelus_brichardi", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes);
    public static final RegistryObject<EntityType<MastacembelusEntity>> SINOBDELLA_SINENSIS = registerEntityType("sinobdella_sinensis", MastacembelusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.3f, MastacembelusEntity::setAttributes);
    public static final RegistryObject<EntityType<RakthamichthysEntity>> RAKTHAMICHTHYS_INDICUS = registerEntityType("rakthamichthys_indicus", RakthamichthysEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.2f, RakthamichthysEntity::setAttributes);
    public static final RegistryObject<EntityType<IndostomusEntity>> INDOSTOMUS_PARADOXUS = registerEntityType("indostomus_paradoxus", IndostomusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.2f, IndostomusEntity::setAttributes);
    public static final RegistryObject<EntityType<RoosterfishEntity>> NEMATISTIUS_PECTORALIS = registerEntityType("nematistius_pectoralis", RoosterfishEntity::new, ModMobCategories.ANIMALIA_FISH, 0.8f, 0.6f, RoosterfishEntity::setAttributes);
    public static final RegistryObject<EntityType<ToxotesEntity>> TOXOTES_CHATAREUS = registerEntityType("toxotes_chatareus", ToxotesEntity::new, ModMobCategories.ANIMALIA_FISH, 0.6f, 0.6f, ToxotesEntity::setAttributes);
    public static final RegistryObject<EntityType<BettaEntity>> BETTA_SPLENDENS = registerEntityType("betta_splendens", BettaEntity::new, ModMobCategories.ANIMALIA_FISH, 0.3f, 0.3f, BettaEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> SCATOPHAGUS_ARGUS = registerEntityType("scatophagus_argus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> POMACANTHUS_IMPERATOR = registerEntityType("pomacanthus_imperator", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> NASO_BREVIROSTRIS = registerEntityType("naso_brevirostris", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> PARACANTHURUS_HEPATUS = registerEntityType("paracanthurus_hepatus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.5f, 0.3f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZANCLUS_CORNUTUS = registerEntityType("zanclus_cornutus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> CHELMON_ROSTRATUS = registerEntityType("chelmon_rostratus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> CHAETODON_AURIGA = registerEntityType("chaetodon_auriga", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<RegSchoolingEntity>> SIGANUS_VULPINUS = registerEntityType("siganus_vulpinus", RegSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, RegSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZEBRASOMA_VELIFER = registerEntityType("zebrasoma_velifer", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.6f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<GrazeSchoolingEntity>> ZEBRASOMA_FLAVESCENS = registerEntityType("zebrasoma_flavescens", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> GITCHAK_NAKANA = registerEntityType("gitchak_nakana", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> CYPRINODON_DIABOLIS = registerEntityType("cyprinodon_diabolis", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> AMBLYOPSIS_HOOSIERI = registerEntityType("amblyopsis_hoosieri", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> KRYPTOGLANIS_SHAJII = registerEntityType("kryptoglanis_shajii", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> SINOCYCLOCHEILUS_LONGICORNUS = registerEntityType("sinocyclocheilus_longicornus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> SINOCYCLOCHEILUS_ANATIROSTRIS = registerEntityType("sinocyclocheilus_anatirostris", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);
    public static final RegistryObject<EntityType<Entity>> SINOCYCLOCHEILUS_HYALINUS = registerEntityType("sinocyclocheilus_hyalinus", GrazeSchoolingEntity::new, ModMobCategories.ANIMALIA_FISH, 0.4f, 0.4f, GrazeSchoolingEntity::setAttributes);

    public static final RegistryObject<EntityType<PangasianodonEntity>> PANGASIANODON_GIGAS = registerEntityType("pangasianodon_gigas", PangasianodonEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, PangasianodonEntity::setAttributes);
    public static final RegistryObject<EntityType<HydrocynusEntity>> HYDROCYNUS_GOLIATH = registerEntityType("hydrocynus_goliath", HydrocynusEntity::new, ModMobCategories.ANIMALIA_FISH, 0.9f, 0.9f, HydrocynusEntity::setAttributes);

    //Non Fish
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_CLARKII = registerEntityType("procambarus_clarkii", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_ALLENI = registerEntityType("procambarus_alleni", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_VIRGINALIS = registerEntityType("procambarus_virginalis", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes);
    public static final RegistryObject<EntityType<CrayfishEntity>> PROCAMBARUS_LUCIFUGUS = registerEntityType("procambarus_lucifugus", CrayfishEntity::new, ModMobCategories.ANIMALIA_INVERTEBRATE, 0.5f, 0.3f, CrayfishEntity::setAttributes);

    //Actual Land animals
    public static final RegistryObject<EntityType<PangolinEntity>> SMUTSIA_GIGANTEA = registerEntityType("smutsia_gigantea", PangolinEntity::new, ModMobCategories.ANIMALIA_LAND, 0.9f, 0.9f, PangolinEntity::setAttributes);


    public static final RegistryObject<EntityType<WaterSpitProjectile>> WATER_SPIT = ENTITY_TYPES.register("water_spit",
            () -> EntityType.Builder.<WaterSpitProjectile>of(WaterSpitProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("water_spit"));

    //Supplier Helper Methods
    public static <T extends Mob> RegistryObject<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float
            width, float height, Supplier<AttributeSupplier> attributes) {
        RegistryObject<EntityType<T>> type = ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
        ATTRIBUTE_SUPPLIERS.put(type, attributes);
        return type;
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
