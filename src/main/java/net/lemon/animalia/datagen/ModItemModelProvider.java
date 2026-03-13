package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Animalia.MODID, existingFileHelper);
    }

    /***
     * Usage Case:
     *  simpleItem(ModItems.ITEM_NAME);
     *
     *  spawn Eggs:
     *  withExistingParent(ModItems.NAME_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
     */
    @Override
    protected void registerModels() {
        //Raws generators
        simpleItem(ModItems.RAW_ICEFISH);
        simpleItem(ModItems.RAW_FISH);

        //Animal Items
        simpleItem(ModItems.AMPHIPOD);
        simpleItem(ModItems.GIGANTOCYPRIS);

        //Misc
        simpleItem(ModItems.FISH_EGG);

        //Buckets
        simpleItem(ModItems.CHILEANSEABASS_BUCKET);
        simpleItem(ModItems.ELEGINOPS_MACLOVINUS_BUCKET);

        //spawn egg generators
        withExistingParent(ModItems.CHILEANSEABASS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ELEGINOPS_MACLOVINUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "item/" + item.getId().getPath()));
    }
}
