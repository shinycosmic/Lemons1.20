package net.lemon.animalia.datagen;

import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags) {
        super(pOutput, pLookupProvider, pBlockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ItemTags.FISHES).add(ModItems.RAW_FISH.get());
        tag(ItemTags.FISHES).add(ModItems.RAW_ICEFISH.get());
        tag(ItemTags.FISHES).add(ModItems.FISH_FOOD.get());

        tag(ModTags.Items.CRUSTACEAN).add(ModItems.AMPHIPOD.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.GIGANTOCYPRIS.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.FISH_FOOD.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.ARTEMIA.get());

        tag(ModTags.Items.FISH_FOOD).add(ModItems.ARTEMIA.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.FISH_FOOD.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.GIGANTOCYPRIS.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.AMPHIPOD.get());
        tag(ModTags.Items.FISH_FOOD).add(Items.KELP);
        tag(ModTags.Items.FISH_FOOD).add(Items.WHEAT_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.BEETROOT_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.MELON_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.PUMPKIN_SEEDS);
    }
}
