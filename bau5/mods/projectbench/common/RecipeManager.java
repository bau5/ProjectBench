package bau5.mods.projectbench.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeManager {
	private List<IRecipe> defaultRecipes;
//	private IdentityHashMap<IdentityHashMap<ItemStack, Integer>, RecipeItem> orderedRecipes;
	private List<RecipeItem> orderedRecipes;
	private long timeStart, timeEnd;
	private static RecipeManager instance;
	
	public RecipeManager(){
		defaultRecipes = CraftingManager.getInstance().getRecipeList();
		associateRecipes();
		defaultRecipes = null;
		instance = this;
	}
	
	public void associateRecipes(){
		orderedRecipes = new ArrayList<RecipeItem>();
		RecipeItem potentialRecipe = null;
		for(IRecipe rec : defaultRecipes){
			potentialRecipe = translateRecipe(rec);
			if(potentialRecipe != null){
				potentialRecipe.postInit();
				orderedRecipes.add(potentialRecipe);
			}
		}
	}

	public ItemStack[] getComponentsToConsume(ItemStack stack) {
		ItemStack[] itemsToConsume = null;
		for(RecipeItem rec : orderedRecipes){
			if(ItemStack.areItemStacksEqual(stack, rec.result())){
				itemsToConsume = rec.items();
			}
		}
		return itemsToConsume;
	}

	public ArrayList<ItemStack> getValidRecipesByStacks(ItemStack[] stacks){
		ArrayList<ItemStack> validRecipes = new ArrayList<ItemStack>();
		
		ItemStack[] recItems;
		boolean hasMeta = false;
		for(RecipeItem rec : orderedRecipes){
			hasMeta = rec.hasMeta();
			int i = 0;
			recItems = rec.items().clone();
			for(; i < recItems.length; i++){
				for(ItemStack stackInInventory : stacks){
					if(stackInInventory != null){
						if(stackInInventory.getItem().equals(recItems[i].getItem())){
							if(hasMeta){
								if(recItems[i].getItemDamage() != stackInInventory.getItemDamage())
	    							continue;
							}
							if(recItems[i].stackSize <= stackInInventory.stackSize){
								recItems[i].stackSize = 0;
							}
						}
					}
				}
			}
			boolean flag = true;
			for(i = 0; i < recItems.length; i++){
				if(recItems[i].stackSize != 0){
					flag = false;
					break;
				}
			}
			if(flag)
				validRecipes.add(rec.result());
		}
		return validRecipes;
	}

	public void displayRecipeForStack(ItemStack stack) {
		for(RecipeItem rec : orderedRecipes){
			if(ItemStack.areItemStacksEqual(stack, rec.result())){
				System.out.println(" - Recipe for " +rec.result() +" -");
				for(ItemStack is : rec.items()){
					System.out.println("\t" +is);
				}
			}
		}
	}
	
	private RecipeItem translateRecipe(IRecipe rec){
		RecipeItem newRecItem = new RecipeItem();
		if(rec instanceof ShapedRecipes){
			newRecItem.items = ((ShapedRecipes) rec).recipeItems;
		}else if(rec instanceof ShapelessRecipes){
			List ls = ((ShapelessRecipes) rec).recipeItems;
			if(ls.get(0) instanceof ItemStack){
				List<ItemStack> ls2 = ls;
				newRecItem.items = new ItemStack[ls2.size()];
				for(int i = 0; i < ls2.size(); i++){
					newRecItem.items[i] = ls2.get(i);
				}
			}
		}else if(rec instanceof ShapedOreRecipe){
			Object[] objArray = ((ShapedOreRecipe) rec).getInput();
			newRecItem.items = new ItemStack[objArray.length];
			for(int i = 0; i < objArray.length; i++){
				if(objArray[i] instanceof ArrayList){
					newRecItem.items[i] = (ItemStack)((List)objArray[i]).get(0);
				}
				else if(objArray[i] instanceof ItemStack){
					newRecItem.items[i] = (ItemStack)objArray[i];
				}
			}
		}else if(rec instanceof ShapelessOreRecipe){
			List inputList = ((ShapelessOreRecipe) rec).getInput();
			newRecItem.items = new ItemStack[inputList.size()];
			for(int i = 0; i < inputList.size(); i++){
				if(inputList.get(i) instanceof ArrayList){
					if(inputList.get(0) instanceof ArrayList){
						newRecItem.items[i] = (ItemStack)((List)(inputList).get(0)).get(0);
					}
				}
				if(inputList.get(i) instanceof ItemStack){
					newRecItem.items[i] = (ItemStack)inputList.get(i);
				}
			}
		}
		else{
			System.out.println("Recipe type not accounted for: " +rec.getRecipeOutput());
		}
		
		if(newRecItem.items == null){
			System.out.println("Recipe for " +newRecItem.result +" has no components.");
			newRecItem = null;
		}else{
			newRecItem.result = rec.getRecipeOutput();
			newRecItem.recipe = rec;
		}
		return newRecItem;
	}
		
	class RecipeItem{
		private ItemStack[] items;
		private Object[] input;
		private IRecipe recipe;
		private ItemStack result;
		private boolean isMetadataSensitive = false;
				
		public RecipeItem(){
			
		}
		
		public void postInit(){
			consolidateStacks();
		}
		
		private void consolidateStacks(){
			List<ItemStack> consolidatedItems = new ArrayList();
			main : for(ItemStack stackInArray : items){
				if(stackInArray == null)
					continue main;
				if(result.getHasSubtypes())
					isMetadataSensitive = true;
				if(consolidatedItems.size() == 0)
					consolidatedItems.add(stackInArray.copy());
				else{
					int counter = 0;
					for(ItemStack stackInList : consolidatedItems){
						counter++;
						if(stackInList.getItem().equals(stackInArray.getItem())){
							stackInList.stackSize++;
							continue main;
						}else if(counter == consolidatedItems.size()){
							consolidatedItems.add(stackInArray.copy());
							continue main;
						}
					}
				}
			}
			System.out.println("Old format.");
			for(ItemStack is : items){
				System.out.println("\t" +is);
			}
			System.out.println("-- New format --");
			int counter = 0;
			items = new ItemStack[consolidatedItems.size()];
			for(ItemStack is3 : consolidatedItems){
				items[counter++] = is3;
				System.out.println(items[counter - 1]);
			}
		}
		
		public ItemStack[] items(){
			ItemStack[] temp = new ItemStack[items.length];
			for(int i = 0; i < temp.length; i++){
				temp[i] = ItemStack.copyItemStack(items[i]);
			}
			return temp;
		}
		public Object[] components(){
			return input.clone();
		}
		public IRecipe recipe(){
			return recipe;
		}
		public ItemStack result(){
			if(result == null)
				return null;
			return result.copy();
		}
		public boolean hasMeta(){
			return isMetadataSensitive;
		}
		
		private void setComponents(Object[] objArray){
			input = objArray.clone();
		}
		private void setItems(ItemStack[] itemArray){
			items = itemArray.clone();
		}
		private void setRecipe(IRecipe rec){
			recipe = rec;
		}
		private void setResult(ItemStack theResult){
			result = theResult.copy();
		}
		
		public boolean hasStackAsComponent(ItemStack stack){
			if(result == null)
				return false;
			boolean hasMeta = false;
			boolean flag = false;
			stack.stackSize = 1;
			for(int i = 0; i < items.length; i++){
				if(items[i] == null)
					continue;
				if(items[i].isItemStackDamageable() && items[i].getMaxDamage() == 0)
					hasMeta = true;
				if(items[i] != null && items[i].itemID == stack.itemID){
					if(hasMeta){
						if(items[i].getItemDamage() == stack.getItemDamage())
							flag = true;
					}
					else{
						flag = true;
					}
					break;
				}
			}
			return flag;
		}
	}
	public static RecipeManager instance(){
		return instance;
	}
}
/*
public ArrayList<ItemStack> getValidRecipesByStacks(ItemStack[] itemStacks){
ArrayList<ItemStack> validRecipes = new ArrayList();
IdentityHashMap<ItemStack, Integer> tempMap = new IdentityHashMap();
System.out.println("Finding recipe that matches " +itemStacks[0] +" as input.");
ItemStack clone = itemStacks[0];
clone.stackSize = 1;
for(Map.Entry<IdentityHashMap<ItemStack, Integer>, RecipeItem> entry : orderedRecipes.entrySet()){
	tempMap = (IdentityHashMap<ItemStack, Integer>) entry.getKey().clone();
	if(ItemStack.areItemStacksEqual(entry.getValue().items[0], itemStacks[0])){

		for(ItemStack is :tempMap.keySet()){
			System.out.print("--" +is +" " +itemStacks[0]);
		}
		System.out.println(tempMap.get(itemStacks[0]));
		System.out.println("Current recipe has " +entry.getValue().items[0] + "\n\tDo these match? " +(ItemStack.areItemStacksEqual(entry.getValue().items[0], itemStacks[0]) ? "Yes" : "No"));
		System.out.println(entry.getKey().get(itemStacks[0]));
		for(Map.Entry<ItemStack, Integer> m : entry.getKey().entrySet()){
			System.out.println(m.getKey() +" " +m.getValue());
		}
	}
}
return validRecipes;
}


private void associateRecipes(){
orderedRecipes = new IdentityHashMap<IdentityHashMap<ItemStack, Integer>, RecipeItem>();
for(IRecipe rec : defaultRecipes){
	RecipeItem potentialRecipe = translateRecipe(rec);
	if(potentialRecipe != null){
		IdentityHashMap<ItemStack, Integer> tempMap = buildMapFromRecipeItem(potentialRecipe);
		orderedRecipes.put(tempMap, potentialRecipe);
	}
}
}

private IdentityHashMap<ItemStack, Integer> buildMapFromRecipeItem(RecipeItem rec){
IdentityHashMap<ItemStack, Integer> tempMap = new HashMap();
ItemStack[]	itemsInRecipe = rec.items();
int count = 0;
for(int i = 0; i < itemsInRecipe.length; i++){
	if(!tempMap.containsKey(itemsInRecipe[i])){
		tempMap.put(itemsInRecipe[i], 1);
	}else{
		count = tempMap.get(itemsInRecipe[i]);
		tempMap.put(itemsInRecipe[i], ++count);
	}
}
return tempMap;
}

public List getValidRecipesByStack(ItemStack stack){
List<ItemStack> validRecipes = new ArrayList();
for(Map.Entry<ItemStack, RecipeItem> entry : orderedRecipes.entrySet()){
	RecipeItem recItem = entry.getValue();
	ItemStack result = recItem.result().copy();
	if(recItem.hasStackAsComponent(stack) && !validRecipes.contains(result)){
		System.out.println(stack +" yields a result of " +result);
		validRecipes.add(result.copy());
		try{
			List<ItemStack> temp = getValidRecipesByStack(result);
			for(ItemStack is : temp){
				if(!validRecipes.contains(is))
					validRecipes.add(is);
			}
		}catch(StackOverflowError er){
			System.out.println(result + " caused error " +recItem.result());
		}
	}
}
return validRecipes;
}
*/