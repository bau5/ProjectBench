package com.bau5.projectbench.common.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Collections;
import java.util.List;

/**
 * Created by bau5 on 5/21/2015.
 */
public class OreDictRecipeHelper {

    // TODO verify that this even works
    public static boolean getIsOreDictAndFill(IRecipe recipe, ItemStack match, List<ItemStack> alternatives) {
        if (!(recipe instanceof ShapedOreRecipe) && !(recipe instanceof ShapelessOreRecipe)) {
            return false;
        }

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.apply(match)) {
                Collections.addAll(alternatives, ingredient.getMatchingStacks());
            }
        }
        return true;
    }
}
