package net.lemon.animalia.registry;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties RAW_ICEFISH = new FoodProperties.Builder()
            .nutrition(4).saturationMod(2.5f).build();
    public static final FoodProperties RAW_FISH = new FoodProperties.Builder()
            .nutrition(2).saturationMod(1.0f).build();
}
