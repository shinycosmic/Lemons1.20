package net.lemon.animalia.registry;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RAW_ICEFISH = new FoodProperties.Builder()
            .nutrition(4).saturationMod(2.5f).build();
    public static final FoodProperties RAW_FISH = new FoodProperties.Builder()
            .nutrition(2).saturationMod(1.0f).build();
    public static final FoodProperties FISH_FOOD = new FoodProperties.Builder()
            .nutrition(1).saturationMod(1.0f).build();
    public static final FoodProperties RAW_VENISON = new FoodProperties.Builder()
            .nutrition(3).saturationMod(2.0f).build();
    public static final FoodProperties COOKED_VENISON = new FoodProperties.Builder()
            .nutrition(6).saturationMod(6.0f).build();
    public static final FoodProperties COOKED_FISH = new FoodProperties.Builder()
            .nutrition(4).saturationMod(4.0f).build();
}
