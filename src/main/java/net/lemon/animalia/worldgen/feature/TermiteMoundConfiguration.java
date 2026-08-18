package net.lemon.animalia.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record TermiteMoundConfiguration(Block sandstone, Block mound, Block borderGround, Block borderSandstone, Block borderMound) implements
        FeatureConfiguration {

    public static final Codec<TermiteMoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("sandstone").forGetter(TermiteMoundConfiguration::sandstone),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("mound").forGetter(TermiteMoundConfiguration::mound),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("border_ground").forGetter(TermiteMoundConfiguration::borderGround),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("border_sandstone").forGetter(TermiteMoundConfiguration::borderSandstone),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("border_mound").forGetter(TermiteMoundConfiguration::borderMound)
    ).apply(instance, TermiteMoundConfiguration::new));
}