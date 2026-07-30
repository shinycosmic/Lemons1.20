package net.lemon.animalia.registry.spawning;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;

import java.util.ArrayList;
import java.util.List;

public class BiomeSelector {
    private final List<TagKey<Biome>> tags = new ArrayList<>();
    private final List<ResourceKey<Biome>> biomes = new ArrayList<>();
    private final List<TagKey<Biome>> excludedTags = new ArrayList<>();
    private final List<ResourceKey<Biome>> excludedBiomes = new ArrayList<>();

    private BiomeSelector() {
    }

    @SafeVarargs
    public static BiomeSelector in(TagKey<Biome>... tags) {
        return new BiomeSelector().or(tags);
    }

    @SafeVarargs
    public static BiomeSelector in(ResourceKey<Biome>... biomes) {
        return new BiomeSelector().or(biomes);
    }

    @SafeVarargs
    public final BiomeSelector or(TagKey<Biome>... tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }

    @SafeVarargs
    public final BiomeSelector or(ResourceKey<Biome>... biomes) {
        this.biomes.addAll(List.of(biomes));
        return this;
    }

    @SafeVarargs
    public final BiomeSelector not(TagKey<Biome>... tags) {
        this.excludedTags.addAll(List.of(tags));
        return this;
    }

    @SafeVarargs
    public final BiomeSelector not(ResourceKey<Biome>... biomes) {
        this.excludedBiomes.addAll(List.of(biomes));
        return this;
    }

    public HolderSet<Biome> resolve(HolderLookup.RegistryLookup<Biome> lookup) {
        HolderSet<Biome> included = union(lookup, this.tags, this.biomes);
        if (included == null) {
            throw new IllegalStateException("BiomeSelector resolved with no tags or biomes");
        }
        HolderSet<Biome> excluded = union(lookup, this.excludedTags, this.excludedBiomes);
        if (excluded == null) {
            return included;
        }
        return new AndHolderSet<>(List.of(included, new NotHolderSet<>(lookup, excluded)));
    }

    private static HolderSet<Biome> union(HolderLookup.RegistryLookup<Biome> lookup,
                                          List<TagKey<Biome>> tags, List<ResourceKey<Biome>> biomes) {
        List<HolderSet<Biome>> parts = new ArrayList<>();
        for (TagKey<Biome> tag : tags) {
            parts.add(lookup.getOrThrow(tag));
        }
        if (!biomes.isEmpty()) {
            List<Holder<Biome>> direct = new ArrayList<>();
            for (ResourceKey<Biome> key : biomes) {
                direct.add(lookup.getOrThrow(key));
            }
            parts.add(HolderSet.direct(direct));
        }
        if (parts.isEmpty()) {
            return null;
        }
        return parts.size() == 1 ? parts.get(0) : new OrHolderSet<>(parts);
    }
}