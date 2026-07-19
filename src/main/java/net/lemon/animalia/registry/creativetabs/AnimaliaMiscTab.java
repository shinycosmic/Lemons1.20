package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaMiscTab {
    public static final List<RegistryObject<? extends Block>> MISC_BLOCKS = List.of(
            //Interactables
            ModBlocks.FILTER_TRAP,
            ModBlocks.ALGAE_MAT

    );

    public static final List<RegistryObject<? extends Item>> MISC_ITEMS = List.of(
            //Interactables
            ModItems.HOLONET

    );

    public static void displayItems(CreativeModeTab.Output output) {
        MISC_ITEMS.forEach(item -> output.accept(item.get()));
        MISC_BLOCKS.forEach(item -> output.accept(item.get()));
    }
}
