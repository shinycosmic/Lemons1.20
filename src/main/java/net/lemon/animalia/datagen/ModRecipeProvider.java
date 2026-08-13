package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    /***
     * Because certain things are hard-coded in vanilla, you will have to copy some vanilla methods to modify
     *
     * Usage Ex:
     *      oreSmelting(pWriter,
     *          [LIST OF ITEMS THAT SMELT INTO THE SAME ITEM],
     *          RecipeCategory.MISC (or anything else),
     *          ModItems.ITEM THIS RECIPE IS FOR,
     *          Experience number (0.25f),
     *          Cooking time (100, 200, etc),
     *          pGroup < IDK what this is, maybe just name it all lowercase "name of item it smelts to")
     *
     *
     *
     * Shaped Recipes and Shapeless Recipes are hit-or-miss with datagen, but effectively the pattern is the 3x3 grid and S is defined as the inputitem
     * You can also define more input items with more Char tags.
     *      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.OUTPUT_ITEM.get()
     *          .pattern("SSS")
     *          .pattern("SSS")
     *          .pattern("SSS")
     *          .define('S', ModItems.INPUT_ITEM.get())
     *          .unlockedBy(getHasName(ModItems.INPUT_ITEM.get()), has(ModItems.INPUT_ITEM.get()))
     *          .save(pWriter);
     *
     *
     *      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.OUTPUT_ITEM.get(), numberToOutput)
     *          .requires(ModBlocks.INPUT_ITEM.get())
     *          .unlockedBy(getHasName(ModBlocks.INPUT_ITEM.get()), has(ModBlocks.INPUT_ITEM.get()))
     *          .save(pWriter);
     *
     * nineBlockStorageRecipes in the RecipeProvider function also does this shapeless with 9 blocks but may need some modifications
     *
     * @param pWriter
     */
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

    }

    // Furnace Recipe
    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    // Blast Furnace Recipe
    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, Animalia.MODID + ":" + (pResult) + pRecipeName + "_" + getItemName(itemlike));
        }

    }
}
