package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
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

        tag(ModTags.Blocks.SHELLS).add(ModBlocks.CREAM_MUSSEL.get());
        tag(ModTags.Blocks.SHELLS).add(ModBlocks.BLACK_MUSSEL.get());
        tag(ModTags.Blocks.SHELLS).add(ModBlocks.BLUE_MUSSEL.get());
        tag(ModTags.Blocks.SHELLS).add(ModBlocks.SWAN_MUSSEL.get());
        tag(ModTags.Blocks.SHELLS).add(ModBlocks.ALGAE_CRUSTED_MUSSEL.get());
        tag(ModTags.Blocks.SHELLS).add(ModBlocks.YELLOW_MUSSEL.get());

        tag(ModTags.Blocks.TERMITE_MOUNDS).add(ModBlocks.TERMITE_MOUND.get());
        tag(ModTags.Blocks.TERMITE_MOUNDS).add(ModBlocks.RED_TERMITE_MOUND.get());

        tag(ModTags.Blocks.CROSS_PLANTS).add(ModBlocks.SAGITTARIA.get());
        tag(ModTags.Blocks.CROSS_PLANTS).add(ModBlocks.KAEMPFERIA_PULCHRA.get());
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.GRASS);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.TALL_GRASS);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.FERN);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.LARGE_FERN);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.DEAD_BUSH);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.ROSE_BUSH);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.PITCHER_PLANT);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.SUNFLOWER);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.LILAC);
        tag(ModTags.Blocks.CROSS_PLANTS).add(Blocks.PEONY);

        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.SEAGRASS);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.TALL_SEAGRASS);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.HORN_CORAL);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BRAIN_CORAL);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.TUBE_CORAL);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.FIRE_CORAL);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BUBBLE_CORAL);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.HORN_CORAL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BRAIN_CORAL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.TUBE_CORAL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.FIRE_CORAL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BUBBLE_CORAL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.HORN_CORAL_WALL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BRAIN_CORAL_WALL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.TUBE_CORAL_WALL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.FIRE_CORAL_WALL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.BUBBLE_CORAL_WALL_FAN);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.KELP);
        tag(ModTags.Blocks.AQUATIC_PLANTS).add(Blocks.KELP_PLANT);

        tag(ModTags.Blocks.FORAGEABLE).addTag(ModTags.Blocks.CROSS_PLANTS);
        tag(ModTags.Blocks.FORAGEABLE).addTag(BlockTags.DIRT);
        tag(ModTags.Blocks.FORAGEABLE).addTag(BlockTags.LEAVES);
        tag(ModTags.Blocks.FORAGEABLE).addTag(BlockTags.CROPS);

        tag(BlockTags.MINEABLE_WITH_SHOVEL).addTag(ModTags.Blocks.TERMITE_MOUNDS);

    }
}
