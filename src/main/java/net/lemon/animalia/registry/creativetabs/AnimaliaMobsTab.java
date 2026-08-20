package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.registry.ModEntities;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class AnimaliaMobsTab {

    public static void displayItems(CreativeModeTab.Output output) {
        for (RegistryObject<Item> bucket : ModEntities.BUCKETS.getEntries()) {
            output.accept(bucket.get());
        }
        for (RegistryObject<Item> egg : ModEntities.SPAWN_EGGS.getEntries()) {
            output.accept(egg.get());
        }
    }
}