package net.lemon.animalia.registry.worldgen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class ModConfiguredFeatures {
    private static final Block[] FRESHWATER_BEDS = {
            Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.SAND,
            Blocks.CLAY, Blocks.MUD, Blocks.STONE, Blocks.DEEPSLATE};

    public static final ResourceKey<ConfiguredFeature<?, ?>> ALGAE_MAT = createKey("algae_mat");

    //Define worldgen configurations here
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        registerMultiface(context, ALGAE_MAT, (MultifaceBlock) ModBlocks.ALGAE_MAT.get(), 4, 0.6F, FRESHWATER_BEDS);
    }

    private static void registerMultiface(BootstapContext<ConfiguredFeature<?, ?>> context,
                                          ResourceKey<ConfiguredFeature<?, ?>> key, MultifaceBlock block,
                                          int searchRange, float chanceOfSpreading, Block... substrate) {
        context.register(key, new ConfiguredFeature<>(Feature.MULTIFACE_GROWTH,
                new MultifaceGrowthConfiguration(block, searchRange, true, false, true, chanceOfSpreading,
                        HolderSet.direct(Block::builtInRegistryHolder, substrate))));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Animalia.MODID, name));
    }
}