package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Animalia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.ACACIA_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.OAK_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.SPRUCE_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.BIRCH_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.JUNGLE_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.DARK_OAK_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.CHERRY_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.MANGROVE_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.AZALEA_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.FLOWERING_AZALEA_LEAVES);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.VINE);
        tag(ModTags.Blocks.FOLIAGE).add(Blocks.LILY_PAD);

    }
}
