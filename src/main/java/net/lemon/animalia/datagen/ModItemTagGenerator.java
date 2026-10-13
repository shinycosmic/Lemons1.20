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
        tag(ItemTags.FISHES)
                .add(ModItems.RAW_FISH.get())
                .add(ModItems.RAW_ICEFISH.get());

        tag(Tags.Items.EGGS)
                .add(ModItems.FISH_EGG.get())
                .add(ModItems.MOUND_FISH_EGG.get())
                .add(ModItems.BETTA_FISH_EGG.get())
                .add(ModItems.AMPHIBIAN_EGG.get());

        tag(ModTags.Items.TO_COOKED_FISH)
                .add(ModItems.RAW_FISH.get())
                .add(ModItems.RAW_ICEFISH.get());

        tag(ModTags.Items.RAW_CRUSTACEAN)
                .add(ModItems.PROCAMBARUS_CLARKII.get())
                .add(ModItems.PROCAMBARUS_ALLENI.get())
                .add(ModItems.PROCAMBARUS_VIRGINALIS.get())
                .add(ModItems.PROCAMBARUS_LUCIFUGUS.get());

        tag(ModTags.Items.CRUSTACEAN)
                .addTag(ModTags.Items.RAW_CRUSTACEAN)
                .add(ModItems.AMPHIPOD.get())
                .add(ModItems.GIGANTOCYPRIS.get())
                .add(ModItems.ARTEMIA.get())
                .add(ModItems.RAW_CRUSTACEAN.get());

        tag(ModTags.Items.INVERTEBRATE)
                .addTag(ModTags.Items.CRUSTACEAN)
                .add(ModItems.TERMITE.get())
                .add(ModItems.WORM.get())
                .add(ModItems.COOKED_CRUSTACEAN.get());

        tag(ModTags.Items.AQUATIC_PLANT)
                .add(Items.KELP)
                .add(Items.SEAGRASS)
                .add(ModBlocks.ALGAE_MAT.get().asItem());

        tag(ModTags.Items.FISH_FOOD)
                .addTag(Tags.Items.SEEDS)
                .addTag(ModTags.Items.AQUATIC_PLANT)
                .add(ModItems.FISH_FOOD.get())
                .add(ModItems.WORM.get())
                .add(ModItems.ARTEMIA.get())
                .add(ModItems.GIGANTOCYPRIS.get())
                .add(ModItems.AMPHIPOD.get())
                .add(ModItems.RAW_CRUSTACEAN.get());

        tag(ModTags.Items.BIVALVES)
                .add(ModBlocks.CREAM_MUSSEL.get().asItem())
                .add(ModBlocks.YELLOW_MUSSEL.get().asItem())
                .add(ModBlocks.BLUE_MUSSEL.get().asItem())
                .add(ModBlocks.BLACK_MUSSEL.get().asItem())
                .add(ModBlocks.ALGAE_CRUSTED_MUSSEL.get().asItem())
                .add(ModBlocks.SWAN_MUSSEL.get().asItem());

        tag(ModTags.Items.DUROPHAGOUS)
                .addTag(ModTags.Items.BIVALVES)
                .add(ModItems.BIVALVE_MEAT.get())
                .add(ModBlocks.PING_PONG_TREE_SPONGE.get().asItem())
                .add(ModBlocks.HARP_SPONGE.get().asItem())
                .add(Items.SPONGE)
                .add(Items.NAUTILUS_SHELL)
                .add(Items.HORN_CORAL, Items.HORN_CORAL_BLOCK, Items.HORN_CORAL_FAN)
                .add(Items.BRAIN_CORAL, Items.BRAIN_CORAL_BLOCK, Items.BRAIN_CORAL_FAN)
                .add(Items.TUBE_CORAL, Items.TUBE_CORAL_BLOCK, Items.TUBE_CORAL_FAN)
                .add(Items.FIRE_CORAL, Items.FIRE_CORAL_BLOCK, Items.FIRE_CORAL_FAN)
                .add(Items.BUBBLE_CORAL, Items.BUBBLE_CORAL_BLOCK, Items.BUBBLE_CORAL_FAN)
                .add(Items.DEAD_HORN_CORAL, Items.DEAD_HORN_CORAL_BLOCK, Items.DEAD_HORN_CORAL_FAN)
                .add(Items.DEAD_BRAIN_CORAL, Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_FAN)
                .add(Items.DEAD_TUBE_CORAL, Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_TUBE_CORAL_FAN)
                .add(Items.DEAD_FIRE_CORAL, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_FAN)
                .add(Items.DEAD_BUBBLE_CORAL, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_FAN);

        tag(ModTags.Items.FRUITS_SEEDS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.SEEDS)
                .add(Items.APPLE)
                .add(Items.MELON_SLICE)
                .add(Items.PUMPKIN)
                .add(Items.COCOA_BEANS)
                .add(Items.SWEET_BERRIES);

        tag(ModTags.Items.LAND_MEATS)
                .add(Items.BEEF)
                .add(Items.PORKCHOP)
                .add(Items.CHICKEN)
                .add(Items.MUTTON)
                .add(Items.RABBIT)
                .add(ModItems.RAW_VENISON.get());

        tag(ModTags.Items.MEATS)
                .addTag(ModTags.Items.LAND_MEATS)
                .addTag(ItemTags.FISHES);

        tag(ModTags.Items.SEAFOOD)
                .addTag(ItemTags.FISHES)
                .addTag(ModTags.Items.BIVALVES)
                .addTag(ModTags.Items.CRUSTACEAN)
                .add(ModItems.BIVALVE_MEAT.get());
    }
}
