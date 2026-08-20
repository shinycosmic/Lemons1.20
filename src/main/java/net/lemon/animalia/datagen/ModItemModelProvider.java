package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Animalia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //Food generators
        simpleItem(ModItems.RAW_ICEFISH);
        simpleItem(ModItems.RAW_FISH);
        simpleItem(ModItems.RAW_VENISON);
        simpleItem(ModItems.RAW_CRUSTACEAN);
        simpleItem(ModItems.COOKED_FISH);
        simpleItem(ModItems.COOKED_VENISON);
        simpleItem(ModItems.COOKED_CRUSTACEAN);
        simpleItem(ModItems.BIVALVE_MEAT);

        //Animal Items
        simpleItem(ModItems.AMPHIPOD);
        simpleItem(ModItems.GIGANTOCYPRIS);
        simpleItem(ModItems.TADPOLE);
        simpleItem(ModItems.ARTEMIA);
        simpleItem(ModItems.TERMITE);
        simpleItem(ModItems.WORM);
        simpleItem(ModItems.PROCAMBARUS_VIRGINALIS);
        simpleItem(ModItems.PROCAMBARUS_ALLENI);
        simpleItem(ModItems.PROCAMBARUS_CLARKII);
        simpleItem(ModItems.PROCAMBARUS_LUCIFUGUS);

        //Misc
        simpleItem(ModItems.FISH_EGG);
        simpleItem(ModItems.MOUND_FISH_EGG);
        simpleItem(ModItems.BETTA_FISH_EGG);
        simpleItem(ModItems.FISH_FOOD);
        simpleItem(ModItems.HOLONET);

        //Block Items
        blockSpriteItem(ModBlocks.KAEMPFERIA_PULCHRA);
        blockSpriteItem(ModBlocks.SAGITTARIA);
        blockSpriteItem(ModBlocks.BLUE_MUSSEL);
        blockSpriteItem(ModBlocks.SWAN_MUSSEL);
        blockSpriteItem(ModBlocks.ALGAE_CRUSTED_MUSSEL);
        blockSpriteItem(ModBlocks.YELLOW_MUSSEL);
        blockSpriteItem(ModBlocks.CREAM_MUSSEL);
        blockSpriteItem(ModBlocks.BLACK_MUSSEL);

        for (RegistryObject<Item> bucket : ModEntities.BUCKETS.getEntries()) {
            simpleItem(bucket);
        }

        for (RegistryObject<Item> egg : ModEntities.SPAWN_EGGS.getEntries()) {
            withExistingParent(egg.getId().getPath(), mcLoc("item/template_spawn_egg"));
        }
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder blockSpriteItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "item/" + item.getId().getPath()));
    }
}
