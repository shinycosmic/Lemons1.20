package net.lemon.animalia.registry.worldgen;

import net.lemon.animalia.Animalia;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ALGAE_MAT = createKey("algae_mat");
    public static final ResourceKey<PlacedFeature> KAEMPFERIA_PULCHRA = createKey("kaempferia_pulchra");
    public static final ResourceKey<PlacedFeature> SAGITTARIA = createKey("sagittaria");
    public static final ResourceKey<PlacedFeature> MARINE_MUSSELS = createKey("marine_mussels");
    public static final ResourceKey<PlacedFeature> FRESHWATER_MUSSELS = createKey("freshwater_mussels");
    public static final ResourceKey<PlacedFeature> TERMITE_MOUND = createKey("termite_mound");
    public static final ResourceKey<PlacedFeature> RED_TERMITE_MOUND = createKey("red_termite_mound");

    //define worldgen here for blocks
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        register(context, ALGAE_MAT, ModConfiguredFeatures.ALGAE_MAT, waterCluster(3, 3, 5));
        register(context, KAEMPFERIA_PULCHRA, ModConfiguredFeatures.KAEMPFERIA_PULCHRA, patch(8, Heightmap.Types.MOTION_BLOCKING));
        register(context, SAGITTARIA, ModConfiguredFeatures.SAGITTARIA, patch(16, Heightmap.Types.OCEAN_FLOOR_WG));
        register(context, MARINE_MUSSELS, ModConfiguredFeatures.MARINE_MUSSELS, waterCluster(3, 3, 5, inWater()));
        register(context, FRESHWATER_MUSSELS, ModConfiguredFeatures.FRESHWATER_MUSSELS, waterCluster(3, 3, 5, inWater()));
        register(context, TERMITE_MOUND, ModConfiguredFeatures.TERMITE_MOUND, patch(24, Heightmap.Types.WORLD_SURFACE_WG));
        register(context, RED_TERMITE_MOUND, ModConfiguredFeatures.RED_TERMITE_MOUND, patch(24, Heightmap.Types.WORLD_SURFACE_WG));
    }





    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 ResourceKey<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placements) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(key, new PlacedFeature(features.getOrThrow(feature), placements));
    }

    private static List<PlacementModifier> waterCluster(int rarity, int minCount, int maxCount, PlacementModifier... extras) {
        return buildPlacements(List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
                CountPlacement.of(UniformInt.of(minCount, maxCount)),
                RandomOffsetPlacement.of(ClampedNormalInt.of(0.0F, 1.5F, -3, 3), ConstantInt.of(0))), extras);
    }

    private static List<PlacementModifier> patch(int rarity, Heightmap.Types heightmap, PlacementModifier... extras) {
        return buildPlacements(List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(heightmap)), extras);
    }

    private static List<PlacementModifier> buildPlacements(List<PlacementModifier> base, PlacementModifier... extras) {
        List<PlacementModifier> placements = new ArrayList<>(base);
        placements.addAll(List.of(extras));
        placements.add(BiomeFilter.biome());
        return placements;
    }

    private static PlacementModifier inWater() {
        return BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Animalia.MODID, name));
    }
}