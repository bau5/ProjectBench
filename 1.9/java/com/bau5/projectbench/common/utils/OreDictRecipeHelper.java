package com.bau5.projectbench.common.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bau5 on 5/21/2015.
 */
public class OreDictRecipeHelper {

    public static boolean getIsOreDictAndFill(IRecipe recipe, int indexInRecipe, ItemStack match, List<ItemStack> alternatives) {
        boolean useOreDict = false;
        ArrayList<ItemStack> alts = new ArrayList<ItemStack>();
        if (recipe instanceof ShapedOreRecipe) {
            Object[] inputList = ((ShapedOreRecipe) recipe).getInput();
            if (inputList[indexInRecipe] instanceof ItemStack) {
                useOreDict = false;
            } else {
                int[] ids2 = OreDictionary.getOreIDs(match);
                for (int id : ids2) {
                    alts.addAll(OreDictionary.getOres(OreDictionary.getOreName(id)));
                }
                useOreDict = true;
            }
        } else if (recipe instanceof ShapelessOreRecipe) {
            List<Object> inputList = ((ShapelessOreRecipe) recipe).getInput();
            if (inputList != null) {
                if (inputList.get(indexInRecipe) instanceof ItemStack) {
                    useOreDict = false;
                } else {
                    int[] ids2 = OreDictionary.getOreIDs(match);
                    for (int id : ids2) {
                        alts.addAll(OreDictionary.getOres(OreDictionary.getOreName(id)));
                    }
                    useOreDict = true;
                }
            }
        }
        alternatives.addAll(alts);
        return useOreDict;
    }
}
