package net.lemon.animalia.registry.worldgen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModFeatures;
import net.lemon.animalia.worldgen.feature.TermiteMoundConfiguration;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.material.Fluids;

public class ModConfiguredFeatures {
    private static final Block[] FRESHWATER_BEDS = {
            Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.SAND,
            Blocks.CLAY, Blocks.MUD, Blocks.STONE, Blocks.DEEPSLATE};

    public static final ResourceKey<ConfiguredFeature<?, ?>> ALGAE_MAT = createKey("algae_mat");
    public static final ResourceKey<ConfiguredFeature<?, ?>> KAEMPFERIA_PULCHRA = createKey("kaempferia_pulchra");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAGITTARIA = createKey("sagittaria");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MARINE_MUSSELS = createKey("marine_mussels");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FRESHWATER_MUSSELS = createKey("freshwater_mussels");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TERMITE_MOUND = createKey("termite_mound");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_TERMITE_MOUND = createKey("red_termite_mound");


    //Define worldgen configurations here
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, ALGAE_MAT, Feature.MULTIFACE_GROWTH, multiface((MultifaceBlock) ModBlocks.ALGAE_MAT.get(), 4, 0.6F, FRESHWATER_BEDS));
        FeatureUtils.register(context, KAEMPFERIA_PULCHRA, Feature.FLOWER, flowerPatch(ModBlocks.KAEMPFERIA_PULCHRA.get(), 64));
        FeatureUtils.register(context, SAGITTARIA, Feature.RANDOM_PATCH, semiaquaticPatch(ModBlocks.SAGITTARIA.get(), 64));
        FeatureUtils.register(context, MARINE_MUSSELS, Feature.SIMPLE_BLOCK, waterFloorBlocks(ModBlocks.BLUE_MUSSEL.get(), ModBlocks.ALGAE_CRUSTED_MUSSEL.get(), ModBlocks.BLACK_MUSSEL.get()));
        FeatureUtils.register(context, FRESHWATER_MUSSELS, Feature.SIMPLE_BLOCK, waterFloorBlocks(ModBlocks.YELLOW_MUSSEL.get(), ModBlocks.ALGAE_CRUSTED_MUSSEL.get(), ModBlocks.BLACK_MUSSEL.get(), ModBlocks.CREAM_MUSSEL.get(), ModBlocks.SWAN_MUSSEL.get()));
        FeatureUtils.register(context, TERMITE_MOUND, ModFeatures.TERMITE_MOUND.get(), new TermiteMoundConfiguration(Blocks.SMOOTH_SANDSTONE, ModBlocks.TERMITE_MOUND.get(), Blocks.RED_SAND, Blocks.SMOOTH_RED_SANDSTONE, ModBlocks.RED_TERMITE_MOUND.get()));
        FeatureUtils.register(context, RED_TERMITE_MOUND, ModFeatures.TERMITE_MOUND.get(), new TermiteMoundConfiguration(Blocks.SMOOTH_RED_SANDSTONE, ModBlocks.RED_TERMITE_MOUND.get(), Blocks.SAND, Blocks.SMOOTH_SANDSTONE, ModBlocks.TERMITE_MOUND.get()));
    }





    private static MultifaceGrowthConfiguration multiface(MultifaceBlock block, int searchRange,
                                                          float chanceOfSpreading, Block... substrate) {
        return new MultifaceGrowthConfiguration(block, searchRange, true, false, true, chanceOfSpreading,
                HolderSet.direct(Block::builtInRegistryHolder, substrate));
    }

    private static RandomPatchConfiguration flowerPatch(Block block, int tries) {
        return FeatureUtils.simpleRandomPatchConfiguration(tries,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(block))));
    }

    private static RandomPatchConfiguration semiaquaticPatch(Block block, int tries) {
        return FeatureUtils.simpleRandomPatchConfiguration(tries,
                PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(block)),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER))));
    }

    private static SimpleBlockConfiguration waterFloorBlocks(Block... blocks) {
        SimpleWeightedRandomList.Builder<BlockState> states = SimpleWeightedRandomList.builder();
        for (Block block : blocks) {
            BlockState state = block.defaultBlockState();
            if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                state = state.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            states.add(state, 1);
        }
        return new SimpleBlockConfiguration(new WeightedStateProvider(states.build()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Animalia.MODID, name));
    }
}