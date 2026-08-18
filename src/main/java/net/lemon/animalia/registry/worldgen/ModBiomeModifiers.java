package net.lemon.animalia.registry.worldgen;

import net.lemon.animalia.Animalia;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.OrHolderSet;

import java.util.ArrayList;
import java.util.List;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ALGAE_MAT = createKey("add_algae_mat");
    public static final ResourceKey<BiomeModifier> ADD_KAEMPFERIA_PULCHRA = createKey("add_kaempferia_pulchra");
    public static final ResourceKey<BiomeModifier> ADD_SAGITTARIA = createKey("add_sagittaria");
    public static final ResourceKey<BiomeModifier> ADD_MARINE_MUSSEL = createKey("add_marine_mussel");
    public static final ResourceKey<BiomeModifier> ADD_FRESHWATER_MUSSEL = createKey("add_freshwater_mussel");
    public static final ResourceKey<BiomeModifier> ADD_TERMITE_MOUND = createKey("add_termite_mound");
    public static final ResourceKey<BiomeModifier> ADD_RED_TERMITE_MOUND = createKey("add_red_termite_mound");

    //Defines which biomes a block spawns in.
    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        addFeature(context, ADD_ALGAE_MAT, ModPlacedFeatures.ALGAE_MAT, GenerationStep.Decoration.VEGETAL_DECORATION, BiomeTags.IS_RIVER, Tags.Biomes.IS_SWAMP, BiomeTags.IS_OCEAN);
        addFeature(context, ADD_KAEMPFERIA_PULCHRA, ModPlacedFeatures.KAEMPFERIA_PULCHRA, GenerationStep.Decoration.VEGETAL_DECORATION, Tags.Biomes.IS_SWAMP);
        addFeature(context, ADD_SAGITTARIA, ModPlacedFeatures.SAGITTARIA, GenerationStep.Decoration.VEGETAL_DECORATION, Tags.Biomes.IS_SWAMP);
        addFeature(context, ADD_MARINE_MUSSEL, ModPlacedFeatures.MARINE_MUSSELS, GenerationStep.Decoration.VEGETAL_DECORATION, BiomeTags.IS_OCEAN);
        addFeature(context, ADD_FRESHWATER_MUSSEL, ModPlacedFeatures.FRESHWATER_MUSSELS, GenerationStep.Decoration.VEGETAL_DECORATION, Tags.Biomes.IS_SWAMP, BiomeTags.IS_RIVER);
        addFeature(context, ADD_TERMITE_MOUND, ModPlacedFeatures.TERMITE_MOUND, GenerationStep.Decoration.SURFACE_STRUCTURES, Tags.Biomes.IS_DESERT);
        addFeature(context, ADD_RED_TERMITE_MOUND, ModPlacedFeatures.RED_TERMITE_MOUND, GenerationStep.Decoration.SURFACE_STRUCTURES, BiomeTags.IS_SAVANNA, BiomeTags.IS_BADLANDS);
    }





    @SafeVarargs
    private static void addFeature(BootstapContext<BiomeModifier> context, ResourceKey<BiomeModifier> key,
                                   ResourceKey<PlacedFeature> feature, GenerationStep.Decoration step,
                                   TagKey<Biome>... biomes) {
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> featureLookup = context.lookup(Registries.PLACED_FEATURE);
        List<HolderSet<Biome>> sets = new ArrayList<>();
        for (TagKey<Biome> tag : biomes) {
            sets.add(biomeLookup.getOrThrow(tag));
        }
        context.register(key, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                sets.size() == 1 ? sets.get(0) : new OrHolderSet<>(sets),
                HolderSet.direct(featureLookup.getOrThrow(feature)), step));
    }

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Animalia.MODID, name));
    }
}