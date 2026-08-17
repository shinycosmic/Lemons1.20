package net.lemon.animalia.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record TermiteMoundConfiguration(BlockState sand, BlockState mound, BlockState borderSand, BlockState borderMound) implements FeatureConfiguration {

    public static final Codec<TermiteMoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("sand").forGetter(TermiteMoundConfiguration::sand),
            BlockState.CODEC.fieldOf("mound").forGetter(TermiteMoundConfiguration::mound),
            BlockState.CODEC.fieldOf("border_sand").forGetter(TermiteMoundConfiguration::borderSand),
            BlockState.CODEC.fieldOf("border_mound").forGetter(TermiteMoundConfiguration::borderMound)
    ).apply(instance, TermiteMoundConfiguration::new));
}
