package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Animalia.MODID, existingFileHelper);
    }

    /***
     * Add tags to specific custom tags in this format (Use BlockTags instead of ModTags if it is a vanilla tag)
     * ex: this.tag(ModTags.Blocks.TAG_NAME)
     *          .add(ModBlocks.ITEM_NAME.get()).addTag(Tags.Blocks.NAME_OF_VANILLA_TAG) <- this is to add other ores in the vanilla tag to this custom tag
     *
     *          Can also use
     *          .add(ModBlocks.ITEM1.get(), ModBlocks.ITEM2.get()....etc) <- this should become a list we pass in
     *
     *          WILL EVENTUALLY REFRACTOR THIS SO THAT WE JUST UPDATE A LIST FROM SOMEWHERE ELSE
     * @param pProvider
     */
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {


    }
}
