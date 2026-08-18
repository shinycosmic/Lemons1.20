package net.lemon.animalia.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;

public record TermiteMoundConfiguration(Block sandstone, Block mound, Block borderGround, Block borderSandstone, Block borderMound) implements
        FeatureConfiguration {

    public static final Codec<TermiteMoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.BLOCKS.getCodec().fieldOf("sandstone").forGetter(TermiteMoundConfiguration::sandstone),
            ForgeRegistries.BLOCKS.getCodec().fieldOf("mound").forGetter(TermiteMoundConfiguration::mound),
            ForgeRegistries.BLOCKS.getCodec().fieldOf("border_ground").forGetter(TermiteMoundConfiguration::borderGround),
            ForgeRegistries.BLOCKS.getCodec().fieldOf("border_sandstone").forGetter(TermiteMoundConfiguration::borderSandstone),
            ForgeRegistries.BLOCKS.getCodec().fieldOf("border_mound").forGetter(TermiteMoundConfiguration::borderMound)
    ).apply(instance, TermiteMoundConfiguration::new));
}