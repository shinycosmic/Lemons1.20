package net.lemon.planetearth.datagen;

import net.lemon.planetearth.PlanetEarth;
import net.lemon.planetearth.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PlanetEarth.MODID, existingFileHelper);
    }

    /***
     * Usage Case:
     *  simpleItem(ModItems.ITEM_NAME);
     */
    @Override
    protected void registerModels() {
        simpleItem(ModItems.FISH_FILLET);
        simpleItem(ModItems.RAW_RODENT);

        //spawn egg generators
        withExistingParent(ModItems.OCELLATED_PAMPAS_SNAKE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(PlanetEarth.MODID, "item/" + item.getId().getPath()));
    }
}
