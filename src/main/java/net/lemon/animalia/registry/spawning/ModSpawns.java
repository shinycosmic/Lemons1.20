package net.lemon.animalia.registry.spawning;

import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModSpawns {
    public static final List<SpawnEntry> ENTRIES = new ArrayList<>();

    /***
     * Parameter definitions:
     * Type - the mob to spawn
     * weight - a ratio against other mobs in the same biome. more weight = more slots on the wheel to land on
     * minGroup/maxGroup - spawning group size
     * selector - this is what biomes they spawn in. can be tags and individual biomes.
     * The Y level and time based spawns are defined in the entity classes themselves by ActivityTime and SpawnBand
     */
    static {
        add(ModEntities.DISSOSTICHUS_ELEGINOIDES, 5, 1, 2,
                BiomeSelector.in(ModTags.Biomes.POLAR_OCEAN));
        add(ModEntities.ELEGINOPS_MACLOVINUS, 8, 1, 3,
                BiomeSelector.in(Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH));
        add(ModEntities.PSEUDAPHRITIS_URVILLII, 8, 1, 2,
                BiomeSelector.in(Biomes.FROZEN_RIVER));
        add(ModEntities.PERCOPHIS_BRASILIENSIS, 6, 1, 2,
                BiomeSelector.in(Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN));
        add(ModEntities.POGONOPHRYNE_MARMORATA, 4, 1, 1,
                BiomeSelector.in(ModTags.Biomes.POLAR_OCEAN));
        add(ModEntities.CHAENOCEPHALUS_ACERATUS, 6, 1, 2,
                BiomeSelector.in(ModTags.Biomes.POLAR_OCEAN));
        add(ModEntities.CYGNODRACO_MAWSONI, 4, 1, 2,
                BiomeSelector.in(ModTags.Biomes.POLAR_OCEAN));
        add(ModEntities.SYNBRANCHUS_MARMORATUS, 5, 1, 1,
                BiomeSelector.in(Biomes.SWAMP));
        add(ModEntities.CHAUDHURIA_CAUDATA, 4, 1, 1,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.MASTACEMBELUS_ARMATUS, 5, 1, 1,
                BiomeSelector.in(Biomes.SWAMP, Biomes.RIVER));
        add(ModEntities.MASTACEMBELUS_ERYTHROTAENIA, 4, 1, 1,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.MACROGNATHUS_SIAMENSIS, 6, 1, 2,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP, Biomes.RIVER));
        add(ModEntities.MASTACEMBELUS_BRICHARDI, 4, 1, 1,
                BiomeSelector.in(Biomes.RIVER));
        add(ModEntities.SINOBDELLA_SINENSIS, 5, 1, 1,
                BiomeSelector.in(Biomes.RIVER));
        add(ModEntities.RAKTHAMICHTHYS_INDICUS, 3, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES));
        add(ModEntities.NEMATISTIUS_PECTORALIS, 2, 1, 1,
                BiomeSelector.in(Biomes.LUKEWARM_OCEAN));
        add(ModEntities.TOXOTES_CHATAREUS, 3, 1, 2,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.BETTA_SPLENDENS, 8, 1, 1,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.SCATOPHAGUS_ARGUS, 6, 2, 6,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP, Biomes.RIVER));
        add(ModEntities.NASO_BREVIROSTRIS, 2, 1, 4,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.POMACANTHUS_IMPERATOR, 3, 1, 2,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.ZANCLUS_CORNUTUS, 4, 2, 6,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.PARACANTHURUS_HEPATUS, 3, 1, 2,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.CHELMON_ROSTRATUS, 4, 1, 2,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.CHAETODON_AURIGA, 3, 1, 3,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.SIGANUS_VULPINUS, 3, 1, 4,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.ZEBRASOMA_FLAVESCENS, 3, 2, 4,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.ZEBRASOMA_VELIFER, 2, 1, 3,
                BiomeSelector.in(Biomes.WARM_OCEAN));
        add(ModEntities.PANGASIANODON_GIGAS, 2, 1, 1,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.HYDROCYNUS_GOLIATH, 2, 1, 1,
                BiomeSelector.in(Biomes.RIVER));
        add(ModEntities.INDOSTOMUS_PARADOXUS, 3, 1, 3,
                BiomeSelector.in(Biomes.MANGROVE_SWAMP));
        add(ModEntities.AMBLYOPSIS_HOOSIERI, 6, 1, 1,
                BiomeSelector.in(Biomes.DRIPSTONE_CAVES));
        add(ModEntities.SINOCYCLOCHEILUS_LONGICORNUS, 3, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES));
        add(ModEntities.SINOCYCLOCHEILUS_HYALINUS, 3, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES));
        add(ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS, 2, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES));
        add(ModEntities.GITCHAK_NAKANA, 2, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES));
        add(ModEntities.KRYPTOGLANIS_SHAJII, 1, 1, 1,
                BiomeSelector.in(Biomes.LUSH_CAVES));
        add(ModEntities.CYPRINODON_DIABOLIS, 1, 2, 4,
                BiomeSelector.in(Biomes.DRIPSTONE_CAVES));

        add(ModEntities.PROCAMBARUS_CLARKII, 6, 1, 2,
                BiomeSelector.in(Biomes.SWAMP, Biomes.RIVER));
        add(ModEntities.PROCAMBARUS_ALLENI, 5, 1, 2,
                BiomeSelector.in(Biomes.SWAMP, Biomes.MANGROVE_SWAMP));
        add(ModEntities.PROCAMBARUS_VIRGINALIS, 4, 1, 1,
                BiomeSelector.in(Biomes.RIVER));
        add(ModEntities.PROCAMBARUS_LUCIFUGUS, 3, 1, 1,
                BiomeSelector.in(Biomes.DRIPSTONE_CAVES, Biomes.LUSH_CAVES));

        //land
        add(ModEntities.SMUTSIA_GIGANTEA, 1, 1, 1,
                BiomeSelector.in(BiomeTags.IS_SAVANNA));
    }

    private static void add(RegistryObject<? extends EntityType<?>> type, int weight, int minGroup, int maxGroup, BiomeSelector selector) {
        ENTRIES.add(new SpawnEntry(type, weight, minGroup, maxGroup, selector));
    }

    public record SpawnEntry(RegistryObject<? extends EntityType<?>> type, int weight, int minGroup, int maxGroup,
                             BiomeSelector selector) {
    }
}