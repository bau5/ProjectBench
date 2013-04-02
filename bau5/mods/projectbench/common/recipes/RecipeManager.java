package bau5.mods.projectbench.common.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Handles translating & reformatting recipes as well as
 * searching for recipes and providing stacks for crafting.
 * 
 * @author _bau5
 * 
 */
public class RecipeManager {
	private List<IRecipe> defaultRecipes;
	private List<RecipeItem> orderedRecipes;
	private long timeStart, timeEnd;
	private static RecipeManager instance;
	
	public RecipeManager(){
		defaultRecipes = CraftingManager.getInstance().getRecipeList();
		associateRecipes();
		Collections.sort(orderedRecipes, new PBRecipeSorter());
		indexList();
		displayList();
		defaultRecipes = null;
		instance = this;
	}
	
	/**
	 * Builds the initial list by iterating through the default
	 * recipes and translating them into something easier to
	 * work with (Recipe Items) 
	 */
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
	
	/**
	 * Uses the Java library binary search function to find 
	 * recipes within the list. Since the list can be rather
	 * large and searching could incur a decent performance 
	 * impact, Binary Search is used to drastically reduce
	 * overhead.
	 * 
	 * @param result The resultant stack that we are looking
	 * for a recipe for.
	 * @return The RecipeItem that has the result of the input 
	 * 
	 * 
	 */
	public RecipeItem searchForRecipe(ItemStack result){
		RecipeItem ri = new RecipeItem();
		ri.setResult(result);
		int i = Collections.binarySearch(orderedRecipes, ri, new PBRecipeSorter());
		if(i >= orderedRecipes.size() || i < 0){
			System.out.println("Recipe not found for " +result);
		}
		return orderedRecipes.get(i);
	}
	
	private void indexList(){
		for(int i = 0; i < orderedRecipes.size(); i++){
			orderedRecipes.get(i).setIndex(i);
		}
	}
	private void displayList(){
		System.out.println("Recipes -- ");
		for(RecipeItem ri : orderedRecipes){
			System.out.println(ri.getIndex() + " " +ri.result() +" " +ri.result().itemID);
		}
	}
	
	/**
	 * This is called when the player clicks on an item that they
	 * wish to craft. It returns the array of items that should
	 * be consumed upon crafting. It uses the binary seach method
	 * to find the Recipe Item
	 * 
	 * @param stack The ItemStack that a recipe is needed for
	 * @return An array of ItemStacks that is the recipe items.
	 * 
	 * 
	 * 
	 */
	public ItemStack[] getComponentsToConsume(ItemStack stack) {
		ItemStack[] itemsToConsume = null;
		RecipeItem ri = searchForRecipe(stack);
		itemsToConsume = ri.items();
		return itemsToConsume;
	}

	/**
	 * This method takes all of the items that are in the tile entity
	 * and returns a list of all of the possible crafting outputs that
	 * will be displayed within the gui for the tile entity.
	 * 
	 * @param stacks The array of ItemStacks to find recipes for.
	 * @return A new ArrayList<ItemStack> of all the outputs 
	 * 
	 */
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
	
	/**
	 * This method takes the sloppy format of the IRecipes
	 * and turns them into RecipeItems so that they can be 
	 * dealt with much easier.
	 * 
	 * @param rec The IRecipe to be translated
	 * @return New RecipeItem with the input and output assigned
	 * 
	 */
	private RecipeItem translateRecipe(IRecipe rec){
		RecipeItem newRecItem = new RecipeItem();
		if(rec instanceof ShapedRecipes){
			newRecItem.items = ((ShapedRecipes) rec).recipeItems;
			ItemStack stack = new ItemStack(Block.stairCompactPlanks, 6, 0);
			if(rec.getRecipeOutput().getItem().equals(stack.getItem())){
				System.out.println("Check");
				for(ItemStack is : newRecItem.items()){
					System.out.println("\t" +is);
				}
			}
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
		
	/**
	 * A custom class to interface with the recipes in Minecraft.
	 * Provides for much easier access of required items, output,
	 * metadata and more. 
	 * 
	 * @author bau5
	 *
	 */
	class RecipeItem{
		private ItemStack[] items;
		private Object[] input;
		private IRecipe recipe;
		private ItemStack result;
		private int indexInList;
		private boolean isMetadataSensitive = false;
				
		public RecipeItem(){
			
		}
		
		/**
		 * Called after the creation of a new RecipeItem. Here
		 * as a bridge method to call other initialization type
		 * methods as needed.
		 */
		public void postInit(){
			consolidateStacks();
		}
		/**
		 * Takes the array of ItemStacks, which may be the same
		 * item type spread across multiple stacks, and puts
		 * them all into one stack, otherwise it deletes null
		 * ItemStacks
		 */
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
//			System.out.println("Old format.");
//			for(ItemStack is : items){
//				System.out.println("\t" +is);
//			}
//			System.out.println("-- New format --");
			int counter = 0;
			items = new ItemStack[consolidatedItems.size()];
			for(ItemStack is3 : consolidatedItems){
				items[counter++] = is3;
//				System.out.println(items[counter - 1]);
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
		public int getIndex(){
			return indexInList;
		}
		
		private void setIndex(int index){
			indexInList = index;
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
	}
	public static RecipeManager instance(){
		return instance;
	}
}