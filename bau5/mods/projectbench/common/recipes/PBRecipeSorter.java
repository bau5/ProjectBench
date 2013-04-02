package bau5.mods.projectbench.common.recipes;

import java.util.Comparator;

import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

public class PBRecipeSorter implements Comparator{
	
	public int compare(RecipeItem item1, RecipeItem item2){
		return (item1.result().itemID < item2.result().itemID) ? -1 : (item1.result().itemID==item2.result().itemID ? ((item1.result().getItemDamage() < item2.result().getItemDamage()) ? -1 : (item1.result().getItemDamage() == item2.result().getItemDamage() ? 0 : 1)) : 1);
	}
	
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return compare((RecipeItem)arg0, (RecipeItem)arg1);
	}

}
