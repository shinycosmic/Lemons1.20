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
        //Food generators
        simpleItem(ModItems.RAW_ICEFISH);
        simpleItem(ModItems.RAW_FISH);
        simpleItem(ModItems.RAW_VENISON);
        simpleItem(ModItems.RAW_CRUSTACEAN);
        simpleItem(ModItems.COOKED_FISH);
        simpleItem(ModItems.COOKED_VENISON);
        simpleItem(ModItems.COOKED_CRUSTACEAN);

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
        simpleItem(ModItems.NEMATISTIUS_PECTORALIS_BUCKET);
        simpleItem(ModItems.TOXOTES_CHATAREUS_BUCKET);
        simpleItem(ModItems.POGONOPHRYNE_MARMORATA_BUCKET);
        simpleItem(ModItems.CHAENOCEPHALUS_ACERATUS_BUCKET);
        simpleItem(ModItems.CYGNODRACO_MAWSONI_BUCKET);
        simpleItem(ModItems.SCATOPHAGUS_ARGUS_BUCKET);
        simpleItem(ModItems.PROCAMBARUS_CLARKII_BUCKET);
        simpleItem(ModItems.PROCAMBARUS_LUCIFUGUS_BUCKET);
        simpleItem(ModItems.PROCAMBARUS_ALLENI_BUCKET);
        simpleItem(ModItems.PROCAMBARUS_VIRGINALIS_BUCKET);
        simpleItem(ModItems.PANGASIANODON_GIGAS_BUCKET);

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
        withExistingParent(ModItems.NEMATISTIUS_PECTORALIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.TOXOTES_CHATAREUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.POGONOPHRYNE_MARMORATA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHAENOCEPHALUS_ACERATUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CYGNODRACO_MAWSONI_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SCATOPHAGUS_ARGUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PROCAMBARUS_ALLENI_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PROCAMBARUS_CLARKII_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PROCAMBARUS_LUCIFUGUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PROCAMBARUS_VIRGINALIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PANGASIANODON_GIGAS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));



    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder blockSpriteItem(RegistryObject<Item> item, String blockTextureName) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Animalia.MODID, "block/" + blockTextureName));
    }
}
