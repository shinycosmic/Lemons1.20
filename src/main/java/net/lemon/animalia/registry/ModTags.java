package net.lemon.animalia.registry;

import net.lemon.animalia.Animalia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> FOLIAGE = tag("foliage");
        public static final TagKey<Block> SHELLS = tag("shells");
        public static final TagKey<Block> TERMITE_MOUNDS = tag("termite_mounds");
        public static final TagKey<Block> CROSS_PLANTS = tag("cross_plants");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Animalia.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> CRUSTACEAN = tag("crustacean");
        public static final TagKey<Item> RAW_CRUSTACEAN = tag("raw_crustacean");
        public static final TagKey<Item> FISH_FOOD = tag("fish_food");
        public static final TagKey<Item> INVERTEBRATE = tag("invertebrate");
        public static final TagKey<Item> AQUATIC_PLANT = tag("aquatic_plant");
        public static final TagKey<Item> DUROPHAGOUS = tag("durophagous");
        public static final TagKey<Item> TO_COOKED_FISH = tag("to_cooked_fish");
        public static final TagKey<Item> BIVALVES = tag("bivalves");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(Animalia.MODID, name));
        }
        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> POLAR_OCEAN = tag("spawns/polar_ocean");
        public static final TagKey<Biome> FRESHWATER = tag("spawns/freshwater");

        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, new ResourceLocation(Animalia.MODID, name));
        }
    }
}
