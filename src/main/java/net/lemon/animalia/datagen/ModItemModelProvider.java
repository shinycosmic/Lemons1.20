package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModBlocks;
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

        //Buckets
        simpleItem(ModItems.DISSOSTICHUS_ELEGINOIDES_BUCKET);
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
        simpleItem(ModItems.POMACANTHUS_IMPERATOR_BUCKET);
        simpleItem(ModItems.NASO_BREVIROSTRIS_BUCKET);
        simpleItem(ModItems.ZANCLUS_CORNUTUS_BUCKET);
        simpleItem(ModItems.PARACANTHURUS_HEPATUS_BUCKET);
        simpleItem(ModItems.CHELMON_ROSTRATUS_BUCKET);
        simpleItem(ModItems.CHAETODON_AURIGA_BUCKET);
        simpleItem(ModItems.SIGANUS_VULPINUS_BUCKET);
        simpleItem(ModItems.ZEBRASOMA_FLAVESCENS_BUCKET);
        simpleItem(ModItems.ZEBRASOMA_VELIFER_BUCKET);
        simpleItem(ModItems.HYDROCYNUS_GOLIATH_BUCKET);
        simpleItem(ModItems.INDOSTOMUS_PARADOXUS_BUCKET);

        //spawn egg generators
        withExistingParent(ModItems.DISSOSTICHUS_ELEGINOIDES_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
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
        withExistingParent(ModItems.POMACANTHUS_IMPERATOR_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.NASO_BREVIROSTRIS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ZANCLUS_CORNUTUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PARACANTHURUS_HEPATUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHELMON_ROSTRATUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHAETODON_AURIGA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SIGANUS_VULPINUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ZEBRASOMA_FLAVESCENS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ZEBRASOMA_VELIFER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HYDROCYNUS_GOLIATH_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SMUTSIA_GIGANTEA_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.INDOSTOMUS_PARADOXUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));



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
