package net.lemon.planetearth.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RAW_RODENT = new FoodProperties.Builder()
            .nutrition(2).saturationMod(0.5f).build();
    public static final FoodProperties FISH_FILLET = new FoodProperties.Builder()
            .nutrition(2).saturationMod(0.8f).build();
}
