package net.lemon.planetearth.registry;

import net.lemon.planetearth.Animalia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        //Add Block Tags here

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Animalia.MODID, name));
        }
    }

    public static class Items {
        //Add Item Tags here

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(Animalia.MODID, name));
        }
    }
}
