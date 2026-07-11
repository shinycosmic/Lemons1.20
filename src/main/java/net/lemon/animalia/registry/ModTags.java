package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Animalia.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> CRUSTACEAN = tag("crustacean");
        public static final TagKey<Item> FISH_FOOD = tag("fish_food");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(Animalia.MODID, name));
        }
    }
}
