package net.lemon.animalia.creativetabs;

import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaMiscTab {
    public static final List<RegistryObject<? extends Block>> MISC_ITEMS = List.of(
            //Interactables
            ModBlocks.FILTER_TRAP

    );

    public static void displayItems(CreativeModeTab.Output output) {
        MISC_ITEMS.forEach(item -> output.accept(item.get()));
    }
}
