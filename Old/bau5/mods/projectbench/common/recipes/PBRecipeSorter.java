package bau5.mods.projectbench.common.recipes;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

/**
 * PBRecipeSorter
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class PBRecipeSorter implements Comparator{
	
	public int compare(RecipeItem item1, RecipeItem item2){
		return compareIS(item1.result(), item2.result());
//		if(item1.result().itemID < item2.result().itemID)
//			return -1;
//		else if(item1.result().itemID == item2.result().itemID){
//			if(item1.result().getItemDamage() < item2.result().getItemDamage())
//				return -1;
//			else if(item1.result().getItemDamage() == item2.result().getItemDamage())
////				if(item1.result().stackSize < item2.result().stackSize)
////					return -1;
////				else if(item1.result().stackSize == item2.result().stackSize)
////					return 0;
////				else 
//					return 0;
//			else return 1;
//		}else{
//			return 1;
//		}
//		return (item1.result().itemID < item2.result().itemID) ? -1 : (item1.result().itemID==item2.result().itemID ? ((item1.result().getItemDamage() < item2.result().getItemDamage()) ? -1 : (item1.result().getItemDamage() == item2.result().getItemDamage() ? 0 : 1)) : 1);
	}
	public int compareIS(ItemStack item1, ItemStack item2){
		if(item1 == null || item2 == null)
			return -1;
		return (item1.itemID < item2.itemID) ? -1 : (item1.itemID==item2.itemID ? ((item1.getItemDamage() < item2.getItemDamage()) ? -1 : (item1.getItemDamage() == item2.getItemDamage() ? 0 : 1)) : 1);
	}
	@Override
	public int compare(Object arg0, Object arg1) {
		if(arg0 instanceof RecipeItem)
			return compare((RecipeItem)arg0, (RecipeItem)arg1);
		if(arg0 instanceof ItemStack)
			return compareIS((ItemStack)arg0, (ItemStack)arg1);
		return -1;
	}

}
