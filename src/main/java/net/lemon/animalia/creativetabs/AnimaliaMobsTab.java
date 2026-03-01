package net.lemon.animalia.creativetabs;

import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaMobsTab {
    public static final List<RegistryObject<? extends Item>> SPAWN_EGGS = List.of(
            //Spawn Eggs
            ModItems.CHILEANSEABASS_SPAWN_EGG

    );

    public static void displayItems(CreativeModeTab.Output output) {
        SPAWN_EGGS.forEach(item -> output.accept(item.get()));
    }
}
