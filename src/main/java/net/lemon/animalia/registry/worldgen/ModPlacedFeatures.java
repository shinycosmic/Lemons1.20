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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ALGAE_MAT = createKey("algae_mat");
    public static final ResourceKey<PlacedFeature> KAEMPFERIA_PULCHRA = createKey("kaempferia_pulchra");
    public static final ResourceKey<PlacedFeature> SAGITTARIA = createKey("sagittaria");

    //define worldgen here for blocks
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        registerBedCluster(context, ALGAE_MAT, ModConfiguredFeatures.ALGAE_MAT, 3, 3, 5);
        registerFlowerPatch(context, KAEMPFERIA_PULCHRA, ModConfiguredFeatures.KAEMPFERIA_PULCHRA, 32);
        registerFlowerPatch(context, SAGITTARIA, ModConfiguredFeatures.SAGITTARIA, 32);
    }

    private static void registerBedCluster(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                           ResourceKey<ConfiguredFeature<?, ?>> feature,
                                           int rarity, int minCount, int maxCount) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(key, new PlacedFeature(features.getOrThrow(feature), List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
                CountPlacement.of(UniformInt.of(minCount, maxCount)),
                RandomOffsetPlacement.of(ClampedNormalInt.of(0.0F, 1.5F, -3, 3), ConstantInt.of(0)),
                BiomeFilter.biome())));
    }

    private static void registerFlowerPatch(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                            ResourceKey<ConfiguredFeature<?, ?>> feature, int rarity) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(key, new PlacedFeature(features.getOrThrow(feature), List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
                BiomeFilter.biome())));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Animalia.MODID, name));
    }
}