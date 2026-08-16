package net.lemon.animalia.registry.creativetabs;

import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AnimaliaPlantsTab {
    public static final List<RegistryObject<? extends Block>> PLANTS = List.of(
            ModBlocks.ALGAE_MAT,
            ModBlocks.KAEMPFERIA_PULCHRA,
            ModBlocks.SAGITTARIA,
            ModBlocks.BLUE_MUSSEL,
            ModBlocks.SWAN_MUSSEL,
            ModBlocks.ALGAE_CRUSTED_MUSSEL,
            ModBlocks.CREAM_MUSSEL,
            ModBlocks.BLACK_MUSSEL,
            ModBlocks.YELLOW_MUSSEL

    );

    public static void displayItems(CreativeModeTab.Output output) {
        PLANTS.forEach(item -> output.accept(item.get()));
    }
}
