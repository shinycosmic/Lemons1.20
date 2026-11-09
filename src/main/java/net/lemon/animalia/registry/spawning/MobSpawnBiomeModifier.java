package net.lemon.animalia.registry.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lemon.animalia.config.SpawnConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;

public record MobSpawnBiomeModifier(HolderSet<Biome> biomes, String key, EntityType<?> entity,
                                    int weight, int minCount, int maxCount) implements BiomeModifier {

    public static final Codec<MobSpawnBiomeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(MobSpawnBiomeModifier::biomes),
            Codec.STRING.fieldOf("key").forGetter(MobSpawnBiomeModifier::key),
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(MobSpawnBiomeModifier::entity),
            Codec.INT.fieldOf("weight").forGetter(MobSpawnBiomeModifier::weight),
            Codec.INT.fieldOf("min_count").forGetter(MobSpawnBiomeModifier::minCount),
            Codec.INT.fieldOf("max_count").forGetter(MobSpawnBiomeModifier::maxCount)
    ).apply(instance, MobSpawnBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !this.biomes.contains(biome)) {
            return;
        }
        int configWeight = SpawnConfig.weight(this.key, this.weight);
        if (configWeight > 0) {
            builder.getMobSpawnSettings().addSpawn(this.entity.getCategory(),
                    new MobSpawnSettings.SpawnerData(this.entity, configWeight, this.minCount, this.maxCount));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return BiomeModifiersHandler.CONFIG_SPAWNS.get();
    }
}