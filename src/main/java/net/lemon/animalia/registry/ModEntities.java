package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.entity.custom.*;
import net.lemon.animalia.entity.projectiles.WaterSpitProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Animalia.MODID);

    /// REGISTER ENTITIES BELOW
    public static final RegistryObject<EntityType<ToothfishEntity>> CHILEANSEABASS = registerEntityType("dissostichus_eleginoides", ToothfishEntity::new, MobCategory.CREATURE, 0.9f, 0.9f);
    public static final RegistryObject<EntityType<ToothfishEntity>> ELEGINOPS_MACLOVINUS = registerEntityType("eleginops_maclovinus", ToothfishEntity::new, MobCategory.CREATURE, 0.6f, 0.9f);
    public static final RegistryObject<EntityType<CongolliEntity>> PSEUDAPHRITIS_URVILLII = registerEntityType("pseudaphritis_urvillii", CongolliEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);
    public static final RegistryObject<EntityType<BettaEntity>> BETTA_SPLENDENS = registerEntityType("betta_splendens", BettaEntity::new, MobCategory.CREATURE, 0.3f, 0.3f);
    public static final RegistryObject<EntityType<ToothfishEntity>> PERCOPHIS_BRASILIENSIS = registerEntityType("percophis_brasiliensis", ToothfishEntity::new, MobCategory.CREATURE, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<SynbranchusEntity>> SYNBRANCHUS_MARMORATUS = registerEntityType("synbranchus_marmoratus", SynbranchusEntity::new, MobCategory.CREATURE, 0.9f, 0.5f);
    public static final RegistryObject<EntityType<SynbranchusEntity>> CHAUDHURIA_CAUDATA = registerEntityType("chaudhuria_caudata", SynbranchusEntity::new, MobCategory.CREATURE, 0.6f, 0.5f);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ARMATUS = registerEntityType("mastacembelus_armatus", MastacembelusEntity::new, MobCategory.CREATURE, 0.7f, 0.3f);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_ERYTHROTAENIA = registerEntityType("mastacembelus_erythrotaenia", MastacembelusEntity::new, MobCategory.CREATURE, 0.7f, 0.3f);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MACROGNATHUS_SIAMENSIS = registerEntityType("macrognathus_siamensis", MastacembelusEntity::new, MobCategory.CREATURE, 0.4f, 0.3f);
    public static final RegistryObject<EntityType<MastacembelusEntity>> MASTACEMBELUS_BRICHARDI = registerEntityType("mastacembelus_brichardi", MastacembelusEntity::new, MobCategory.CREATURE, 0.4f, 0.3f);
    public static final RegistryObject<EntityType<MastacembelusEntity>> SINOBDELLA_SINENSIS = registerEntityType("sinobdella_sinensis", MastacembelusEntity::new, MobCategory.CREATURE, 0.4f, 0.3f);
    public static final RegistryObject<EntityType<RakthamichthysEntity>> RAKTHAMICHTHYS_INDICUS = registerEntityType("rakthamichthys_indicus", RakthamichthysEntity::new, MobCategory.CREATURE, 0.3f, 0.2f);
    public static final RegistryObject<EntityType<RoosterfishEntity>> NEMATISTIUS_PECTORALIS = registerEntityType("nematistius_pectoralis", RoosterfishEntity::new, MobCategory.CREATURE, 0.8f, 0.6f);
    public static final RegistryObject<EntityType<ToxotesEntity>> TOXOTES_CHATAREUS = registerEntityType("toxotes_chatareus", ToxotesEntity::new, MobCategory.CREATURE, 0.6f, 0.6f);
    public static final RegistryObject<EntityType<PogonophryneEntity>> POGONOPHRYNE_MARMORATA = registerEntityType("pogonophryne_marmorata", PogonophryneEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CHAENOCEPHALUS_ACERATUS = registerEntityType("chaenocephalus_aceratus", ChaenocephalusEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);
    public static final RegistryObject<EntityType<ChaenocephalusEntity>> CYGNODRACO_MAWSONI = registerEntityType("cygnodraco_mawsoni", ChaenocephalusEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);

    public static final RegistryObject<EntityType<RegSchoolingEntity>> SCATOPHAGUS_ARGUS = registerEntityType("scatophagus_argus", RegSchoolingEntity::new, MobCategory.CREATURE, 0.5f, 0.3f);


    /// REGISTER OTHER ENTITIES BELOW
    public static final RegistryObject<EntityType<WaterSpitProjectile>> WATER_SPIT = ENTITY_TYPES.register("water_spit",
            () -> EntityType.Builder.<WaterSpitProjectile>of(WaterSpitProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("water_spit"));

    //Supplier Helper Methods
    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
