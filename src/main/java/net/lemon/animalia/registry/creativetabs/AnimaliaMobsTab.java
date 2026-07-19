package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaMobsTab {
    public static final List<RegistryObject<? extends Item>> SPAWN_EGGS = List.of(
            //Spawn Eggs
            ModItems.CHILEANSEABASS_SPAWN_EGG,
            ModItems.ELEGINOPS_MACLOVINUS_SPAWN_EGG,
            ModItems.PSEUDAPHRITIS_URVILLII_SPAWN_EGG,
            ModItems.PERCOPHIS_BRASILIENSIS_SPAWN_EGG,
            ModItems.POGONOPHRYNE_MARMORATA_SPAWN_EGG,
            ModItems.CHAENOCEPHALUS_ACERATUS_SPAWN_EGG,
            ModItems.SYNBRANCHUS_MARMORATUS_SPAWN_EGG,
            ModItems.CHAUDHURIA_CAUDATA_SPAWN_EGG,
            ModItems.MASTACEMBELUS_ARMATUS_SPAWN_EGG,
            ModItems.MASTACEMBELUS_ERYTHROTAENIA_SPAWN_EGG,
            ModItems.MASTACEMBELUS_BRICHARDI_SPAWN_EGG,
            ModItems.MACROGNATHUS_SIAMENSIS_SPAWN_EGG,
            ModItems.SINOBDELLA_SINENSIS_SPAWN_EGG,
            ModItems.RAKTHAMICHTHYS_INDICUS_SPAWN_EGG,
            ModItems.NEMATISTIUS_PECTORALIS_SPAWN_EGG,
            ModItems.TOXOTES_CHATAREUS_SPAWN_EGG,
            ModItems.BETTA_SPLENDENS_SPAWN_EGG


    );

    public static void displayItems(CreativeModeTab.Output output) {
        SPAWN_EGGS.forEach(item -> output.accept(item.get()));
    }
}
