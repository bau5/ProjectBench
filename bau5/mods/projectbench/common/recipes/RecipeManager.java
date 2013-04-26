package bau5.mods.projectbench.common.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import bau5.mods.projectbench.common.ProjectBench;
import cpw.mods.ironchest.ItemChestChanger;

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
	private static RecipeManager instance;
	private static boolean DEBUG_MODE = ProjectBench.DEBUG_MODE_ENABLED;
	
	public RecipeManager(){
		defaultRecipes = CraftingManager.getInstance().getRecipeList();
		associateRecipes();
		Collections.sort(orderedRecipes, new PBRecipeSorter());
		indexList();
		displayList();
		defaultRecipes = null;
		instance = this;

		System.out.println("\tRecipe Manager active.");
	}
		
	/**
	 * Builds the initial list by iterating through the default
	 * recipes and translating them into something easier to
	 * work with (Recipe Items) 
	 */
	private void associateRecipes(){
		orderedRecipes = new ArrayList<RecipeItem>();
		RecipeItem potentialRecipe = null;
		for(IRecipe rec : defaultRecipes){
			potentialRecipe = translateRecipe(rec);
			if(potentialRecipe != null){
				if(!checkForRecipe(potentialRecipe))
					orderedRecipes.add(potentialRecipe);
			}
		}
	}
	
	public boolean checkForRecipe(RecipeItem rec){
		RecipeItem dup = null;
		ItemStack result = rec.result();
		int indexInList = 0;
		for(; indexInList < orderedRecipes.size(); indexInList++){
			if(orderedRecipes.get(indexInList) != null && ItemStack.areItemStacksEqual(orderedRecipes.get(indexInList).result, result)){
				dup = orderedRecipes.get(indexInList);
				break;
			}
		}
		if(dup != null){
			if(dup.items != null)
				dup.alternatives.add(dup.items());
			rec.consolidateStacks();
			for(ItemStack[] isa : rec.alternatives)
				if(isa.length > 0)
					dup.alternatives.add(isa);
				else
					print("Recipe " +rec.result() +" has a recipe with no components. Removing.");
			rec.items = null;
			return true;
		}else{
			rec.postInit();
			return false;
		}
	}
	
	//TODO account for stack size differences.
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
		if(i == -1 || i >= orderedRecipes.size() || i < 0){
			print("Recipe not found for " +result);
			return null;
		}
		return orderedRecipes.get(i);
	}
	
	private void indexList(){
		for(int i = 0; i < orderedRecipes.size(); i++){
			orderedRecipes.get(i).setIndex(i);
		}
	}
	private void displayList(){
		print("Recipes -- ");
		for(RecipeItem ri : orderedRecipes){
			print(ri.getIndex() + " " +ri.result() +" " +ri.result().itemID);
		}
	}
	
	/**
	 * This is called when something wants to find the recipe for
	 * a given ItemStack as input. It will return all the possible
	 * ItemStack arrays that craft the provided stack. The list may
	 * have only one recipe in it.
	 * 
	 * @param stack The ItemStack that a recipe is needed for
	 * @return A List of ItemStack arrays that are the recipe items.
	 * 
	 */
	public ArrayList<ItemStack[]> getComponentsToConsume(ItemStack stack) {
		ArrayList<ItemStack[]> itemsToConsume = null;
		if(stack == null)
			return null;
		RecipeItem ri = searchForRecipe(stack);
		if(ri != null)
			itemsToConsume = ri.alternatives();
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
//		if(stacks.length == 0)
//			return new ArrayList<ItemStack>();
		ArrayList<ItemStack> validRecipes = new ArrayList<ItemStack>();
		ArrayList<ItemStack[]> stacksForRecipe = null;
		boolean hasMeta = false;
		boolean flag = true;
		//TODO Did I do this right? Looping through multiple possibilities?
		recLoop : for(RecipeItem rec : orderedRecipes){
			flag = true;
			stacksForRecipe = rec.alternatives();
			hasMeta = rec.hasMeta();
			multiRecipeLoop : for(ItemStack[] recItems : stacksForRecipe){
				for(int i = 0; i < recItems.length; i++){
					for(ItemStack stackInInventory : stacks){
						if(stackInInventory != null){
							if(stackInInventory.getItem().equals(recItems[i].getItem())){
								if(hasMeta){
									if(!OreDictionary.itemMatches(recItems[i], stackInInventory, false))
		    							continue;
								}
								if(recItems[i].getItem().hasContainerItem()){
									continue recLoop;
									// Disabling container recipes for now.
//									ItemStack contItem = recItems[i].getItem().getContainerItemStack(recItems[i]);
//									if(contItem.isItemStackDamageable()){
//										if(contItem.getItemDamage() + 1 <= contItem.getMaxDamage())
//											recItems[i].stackSize = 0;
//										else{
//											recItems[i].setItemDamage(recItems[i].getItemDamage() + 1);
//										}
//									}else{
//										if(contItem.stackSize <= stackInInventory.stackSize){
//											recItems[i].stackSize -= contItem.stackSize;
//										}
//									}
								}else if(recItems[i].stackSize <= stackInInventory.stackSize){
									recItems[i].stackSize = 0;
								}else if(recItems[i].stackSize > stackInInventory.stackSize){
									continue multiRecipeLoop;
								}
							}
						}
					}
					if(recItems[i].stackSize != 0){
						flag = false;
						break;
					}
				}
				if(flag)
					validRecipes.add(rec.result());
			}
		}
		return validRecipes;
	}
	
	public ItemStack[] consolidateItemStacks(ItemStack[] stacks){
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack stack = null;
		for(int i = 0; i < stacks.length; i++){
			if(stacks[i] != null)
				stack = stacks[i].copy();
			if(stack != null){
				items.add(stack);
			}
		}
		List<ItemStack> consolidatedItems = new ArrayList();
		main : for(ItemStack stackInArray : items){
			if(stackInArray == null)
				continue main;
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
		ItemStack[] stacks2 = new ItemStack[consolidatedItems.size()];
		for(int i = 0; i < stacks2.length; i++){
			stacks2[i] = consolidatedItems.get(i);
		}
		return stacks2;
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
		String type = "[null]";
		try{
			RecipeItem newRecItem = new RecipeItem();
			if(rec instanceof ShapedRecipes){
				type = "ShapedRecipes";
				newRecItem.items = ((ShapedRecipes) rec).recipeItems;
			}else if(rec instanceof ShapelessRecipes){
				type = "ShapelessRecipes";
				List ls = ((ShapelessRecipes) rec).recipeItems;
				
				if(ls.size() > 0 && ls.get(0) instanceof ItemStack){
					List<ItemStack> ls2 = ls;
					newRecItem.items = new ItemStack[ls2.size()];
					for(int i = 0; i < ls2.size(); i++){
						newRecItem.items[i] = ls2.get(i);
					}
				}
			}else if(rec instanceof ShapedOreRecipe){
				if(rec.getRecipeOutput().getItem().itemID >= 19501 +256 || rec.getRecipeOutput().getItem().itemID == 975)
					System.out.println("Check");
				type = "ShapedOreRecipe";
				Object[] objArray = ((ShapedOreRecipe) rec).getInput();
				newRecItem.items = new ItemStack[objArray.length];
				for(int i = 0; i < objArray.length; i++){
					if(objArray[i] instanceof ArrayList){
						List theList = ((List)objArray[i]);
						if(((List)objArray[i]) != null && theList.size() > 0){
							newRecItem.items[i] = (ItemStack)theList.get(0);
						}else{
							//Recipe is missing nonexistant ore types
							return null;
						}
					}
					else if(objArray[i] instanceof ItemStack){
						newRecItem.items[i] = (ItemStack)objArray[i];
					}
				}
				boolean flag = false;
				for(ItemStack is : newRecItem.items){
					if(is != null)
						flag = true;
				}
				if(!flag)
					return null;
			}else if(rec instanceof ShapelessOreRecipe){
				type = "ShapelessOreRecipe";
				List inputList = ((ShapelessOreRecipe) rec).getInput();
				newRecItem.items = new ItemStack[inputList.size()];
				for(int i = 0; i < inputList.size(); i++){
					if(inputList.get(i) instanceof ArrayList){
						if(inputList.get(0) instanceof ArrayList){
							if(((List)(inputList).get(0)) != null && ((List)(inputList).get(0)).size() > 0)
								newRecItem.items[i] = (ItemStack)((List)(inputList).get(0)).get(0);
							else
								newRecItem.items[i] = null;
						}
					}
					if(inputList.get(i) instanceof ItemStack){
						newRecItem.items[i] = (ItemStack)inputList.get(i);
					}
				}
			}
			else{
				print("Recipe type not accounted for: " +rec.getRecipeOutput());
			}
			newRecItem.type = type;
			
			if(newRecItem.items == null){
				print("Recipe for " +newRecItem.result +" has no components.");
				newRecItem = null;
			}else if (newRecItem.items.length == 0){
				print("Recipe for " +newRecItem.result +" has an empty recipe array.");
				newRecItem = null;
			}else {
				newRecItem.result = rec.getRecipeOutput();
				newRecItem.recipe = rec;
			}
			return newRecItem;
		}catch(Exception ex){
			System.err.println("Project Bench: Error encountered while translating recipe.");
			System.err.println("\t Recipe for: " +rec.getRecipeOutput());
			System.err.println("\t Recipe type: "+type);
			System.err.println("Please report this on the forums or GitHub.");
			return null;
		}
	}
		//TODO seperate into its own class.
	/**
	 * A custom class to interface with the recipes in Minecraft.
	 * Provides for much easier access of required items, output,
	 * metadata and more. 
	 * 
	 * @author bau5
	 *
	 */
	public class RecipeItem{
		private ItemStack[] items;
		private ArrayList<ItemStack[]> alternatives = new ArrayList<ItemStack[]>();
		private Object[] input;
		private IRecipe recipe;
		private ItemStack result;
		private int indexInList;
		private boolean isMetadataSensitive = false;
		private String type = "[null]";
				
		public RecipeItem(){
			
		}
		
		/**
		 * Called after the creation of a new RecipeItem. Here
		 * as a bridge method to call other initialization type
		 * methods as needed.
		 */
		public void postInit(){
			consolidateStacks();
			if(result.itemID < Block.blocksList.length){
				if(result.itemID < Block.blocksList.length &&
				   Block.blocksList[result.itemID] != null &&
			       Block.blocksList[result.itemID] == Block.stairsWoodOak ||
			       Block.blocksList[result.itemID] == Block.stairsWoodBirch ||
			       Block.blocksList[result.itemID] == Block.stairsWoodSpruce ||
			       Block.blocksList[result.itemID] == Block.stairsWoodJungle ){
					isMetadataSensitive = true;				
				}
			}
		}
		/**
		 * Takes the array of ItemStacks, which may be the same
		 * item type spread across multiple stacks, and puts
		 * them all into one stack, otherwise it deletes null
		 * ItemStacks
		 */
		private void consolidateStacks(){
			List<ItemStack> consolidatedItems = new ArrayList();
			if(items == null)
				return;
			main : for(ItemStack stackInArray : items){
				if(stackInArray == null || stackInArray.getItem() == null)
					continue main;
				if(result.getHasSubtypes())
					isMetadataSensitive = true;
				if(consolidatedItems.size() == 0)
					consolidatedItems.add(stackInArray.copy());
				else{
					int counter = 0;
					list : for(ItemStack stackInList : consolidatedItems){
						if(stackInList == null || stackInArray.getItem() == null)
							continue list;
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
			int counter = 0;
			items = new ItemStack[consolidatedItems.size()];
			for(int i = 0; i < items.length; i++)
				items[i] = consolidatedItems.get(i);
			alternatives.add(items);
			items = null;
		}
		public ArrayList<ItemStack[]> alternatives(){
			ArrayList<ItemStack[]> temp = new ArrayList<ItemStack[]>();
			ItemStack[] newisa = null;
			for(ItemStack[] isa : alternatives){
				newisa = new ItemStack[isa.length];
				for(int i = 0; i < isa.length; i++){
					newisa[i] = ItemStack.copyItemStack(isa[i]);
				}
				temp.add(newisa);
			}
			return temp;
		}
		public ItemStack[] items(){
			ItemStack[] temp = new ItemStack[items.length];
			for(int i = 0; i < temp.length; i++){
				temp[i] = ItemStack.copyItemStack(items[i]);
			}
			return temp;
		}
		public boolean hasAlternatives(){
			return (alternatives != null && alternatives.size() > 1);
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
			return ItemStack.copyItemStack(result);
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
		
		public String toString()
	    {
	        return result + ":" + type +"x" + ((alternatives != null) ? alternatives.size() : 0);
	    }
		
	}
	public static RecipeManager instance(){
		return instance;
	}
	public static void print(String message){
		if(DEBUG_MODE || ProjectBench.DEV_ENV)
			System.out.println(message);
	}
	public static void print(ItemStack stack){
		print("" +stack);
	}
	public static void print(int i){
		print("" +i);
	}
	public static void print(boolean bool){
		print("" +bool);
	}
}