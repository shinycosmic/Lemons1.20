package net.lemon.animalia.datagen;

import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags) {
        super(pOutput, pLookupProvider, pBlockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //forge tags
        tag(ItemTags.FISHES).add(ModItems.RAW_FISH.get());
        tag(ItemTags.FISHES).add(ModItems.RAW_ICEFISH.get());
        tag(ItemTags.FISHES).add(ModItems.FISH_FOOD.get());
        tag(ItemTags.FISHES).add(ModItems.COOKED_FISH.get());

        tag(Tags.Items.EGGS).add(ModItems.FISH_EGG.get());
        tag(Tags.Items.EGGS).add(ModItems.MOUND_FISH_EGG.get());

        tag(ModTags.Items.TO_COOKED_FISH).add(ModItems.RAW_FISH.get());
        tag(ModTags.Items.TO_COOKED_FISH).add(ModItems.RAW_ICEFISH.get());

        tag(ModTags.Items.CRUSTACEAN).add(ModItems.AMPHIPOD.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.GIGANTOCYPRIS.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.ARTEMIA.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.RAW_CRUSTACEAN.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.PROCAMBARUS_CLARKII.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.PROCAMBARUS_ALLENI.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.PROCAMBARUS_VIRGINALIS.get());
        tag(ModTags.Items.CRUSTACEAN).add(ModItems.PROCAMBARUS_LUCIFUGUS.get());

        tag(ModTags.Items.FISH_FOOD).add(ModItems.ARTEMIA.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.WORM.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.FISH_FOOD.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.GIGANTOCYPRIS.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.AMPHIPOD.get());
        tag(ModTags.Items.FISH_FOOD).add(ModItems.RAW_CRUSTACEAN.get());
        tag(ModTags.Items.FISH_FOOD).add(Items.KELP);
        tag(ModTags.Items.FISH_FOOD).add(Items.WHEAT_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.BEETROOT_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.MELON_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.PUMPKIN_SEEDS);
        tag(ModTags.Items.FISH_FOOD).add(Items.SEAGRASS);

        tag(ModTags.Items.INVERTEBRATE).add(ModItems.ARTEMIA.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.GIGANTOCYPRIS.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.AMPHIPOD.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.TERMITE.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.WORM.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.RAW_CRUSTACEAN.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.COOKED_CRUSTACEAN.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.PROCAMBARUS_CLARKII.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.PROCAMBARUS_ALLENI.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.PROCAMBARUS_VIRGINALIS.get());
        tag(ModTags.Items.INVERTEBRATE).add(ModItems.PROCAMBARUS_LUCIFUGUS.get());

        tag(ModTags.Items.AQUATIC_PLANT).add(Items.KELP);
        tag(ModTags.Items.AQUATIC_PLANT).add(Items.SEAGRASS);
        tag(ModTags.Items.AQUATIC_PLANT).add(ModBlocks.ALGAE_MAT.get().asItem());


        tag(ModTags.Items.DUROPHAGOUS).add(Items.HORN_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.HORN_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.HORN_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BRAIN_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BRAIN_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BRAIN_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.TUBE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.TUBE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.TUBE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.FIRE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.FIRE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.FIRE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BUBBLE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BUBBLE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.BUBBLE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_HORN_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_HORN_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_HORN_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BRAIN_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BRAIN_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BRAIN_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_TUBE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_TUBE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_TUBE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BUBBLE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BUBBLE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_BUBBLE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_FIRE_CORAL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_FIRE_CORAL_BLOCK);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.DEAD_FIRE_CORAL_FAN);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.NAUTILUS_SHELL);
        tag(ModTags.Items.DUROPHAGOUS).add(Items.SPONGE);
        tag(ModTags.Items.DUROPHAGOUS).add(ModItems.BIVALVE_MEAT.get());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.CREAM_MUSSEL.get().asItem());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.YELLOW_MUSSEL.get().asItem());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.BLUE_MUSSEL.get().asItem());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.BLACK_MUSSEL.get().asItem());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.ALGAE_CRUSTED_MUSSEL.get().asItem());
        tag(ModTags.Items.DUROPHAGOUS).add(ModBlocks.SWAN_MUSSEL.get().asItem());

        tag(ModTags.Items.BIVALVES).add(ModBlocks.CREAM_MUSSEL.get().asItem());
        tag(ModTags.Items.BIVALVES).add(ModBlocks.YELLOW_MUSSEL.get().asItem());
        tag(ModTags.Items.BIVALVES).add(ModBlocks.BLUE_MUSSEL.get().asItem());
        tag(ModTags.Items.BIVALVES).add(ModBlocks.BLACK_MUSSEL.get().asItem());
        tag(ModTags.Items.BIVALVES).add(ModBlocks.ALGAE_CRUSTED_MUSSEL.get().asItem());
        tag(ModTags.Items.BIVALVES).add(ModBlocks.SWAN_MUSSEL.get().asItem());

        tag(ModTags.Items.RAW_CRUSTACEAN).add(ModItems.PROCAMBARUS_CLARKII.get());
        tag(ModTags.Items.RAW_CRUSTACEAN).add(ModItems.PROCAMBARUS_ALLENI.get());
        tag(ModTags.Items.RAW_CRUSTACEAN).add(ModItems.PROCAMBARUS_VIRGINALIS.get());
        tag(ModTags.Items.RAW_CRUSTACEAN).add(ModItems.PROCAMBARUS_LUCIFUGUS.get());

    }
}
