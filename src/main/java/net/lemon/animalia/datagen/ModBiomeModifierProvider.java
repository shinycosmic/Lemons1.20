package net.lemon.animalia.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.spawning.MobSpawnBiomeModifier;
import net.lemon.animalia.registry.spawning.ModSpawns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBiomeModifierProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public ModBiomeModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "forge/biome_modifier");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.lookupProvider.thenCompose(provider -> {
            HolderLookup.RegistryLookup<Biome> biomes = provider.lookupOrThrow(Registries.BIOME);
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);

            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (ModSpawns.SpawnEntry entry : ModSpawns.ENTRIES) {
                String name = entry.name();

                BiomeModifier modifier = new MobSpawnBiomeModifier(
                        entry.selector().resolve(biomes), name, entry.type().get(),
                        entry.weight(), entry.minGroup(), entry.maxGroup());

                JsonElement json = BiomeModifier.DIRECT_CODEC.encodeStart(ops, modifier)
                        .getOrThrow(false, error -> {
                            throw new IllegalStateException("Failed to encode biome modifier " + name + ": " + error);
                        });
                futures.add(DataProvider.saveStable(cache, json,
                        this.pathProvider.json(new ResourceLocation(Animalia.MODID, name))));
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Animalia Biome Modifiers";
    }
}