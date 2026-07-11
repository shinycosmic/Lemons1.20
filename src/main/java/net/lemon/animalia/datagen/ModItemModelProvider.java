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
        simpleItem(ModItems.RAW_VENISON);

        //Animal Items
        simpleItem(ModItems.AMPHIPOD);
        simpleItem(ModItems.GIGANTOCYPRIS);
        simpleItem(ModItems.TADPOLE);
        simpleItem(ModItems.ARTEMIA);

        //Misc
        simpleItem(ModItems.FISH_EGG);
        simpleItem(ModItems.BETTA_FISH_EGG);
        simpleItem(ModItems.FISH_FOOD);
        simpleItem(ModItems.HOLONET);

        //Buckets
        simpleItem(ModItems.CHILEANSEABASS_BUCKET);
        simpleItem(ModItems.ELEGINOPS_MACLOVINUS_BUCKET);
        simpleItem(ModItems.PSEUDAPHRITIS_URVILLII_BUCKET);
        simpleItem(ModItems.BETTA_SPLENDENS_BUCKET);
        simpleItem(ModItems.PERCOPHIS_BRASILIENSIS_BUCKET);
        simpleItem(ModItems.SYNBRANCHUS_MARMORATUS_BUCKET);
        simpleItem(ModItems.CHAUDHURIA_CAUDATA_BUCKET);
        simpleItem(ModItems.MACROGNATHUS_SIAMENSIS_BUCKET);
        simpleItem(ModItems.MASTACEMBELUS_ARMATUS_BUCKET);
        simpleItem(ModItems.MASTACEMBELUS_ERYTHROTAENIA_BUCKET);
        simpleItem(ModItems.MASTACEMBELUS_BRICHARDI_BUCKET);
        simpleItem(ModItems.SINOBDELLA_SINENSIS_BUCKET);
        simpleItem(ModItems.RAKTHAMICHTHYS_INDICUS_BUCKET);


        //spawn egg generators
        withExistingParent(ModItems.CHILEANSEABASS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ELEGINOPS_MACLOVINUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PSEUDAPHRITIS_URVILLII_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BETTA_SPLENDENS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PERCOPHIS_BRASILIENSIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SYNBRANCHUS_MARMORATUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHAUDHURIA_CAUDATA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MASTACEMBELUS_ERYTHROTAENIA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MASTACEMBELUS_ARMATUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MASTACEMBELUS_BRICHARDI_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MACROGNATHUS_SIAMENSIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SINOBDELLA_SINENSIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.RAKTHAMICHTHYS_INDICUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "item/" + item.getId().getPath()));
    }
}
