package bau5.mods.projectbench.common.recipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.item.crafting.RecipesArmor;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.item.crafting.RecipesMapCloning;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import bau5.mods.projectbench.common.ProjectBench;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.ICraftingHandler;

/**
 * RecipeManager
 * 
 * Handles translating & reformatting recipes as well as
 * searching for recipes and providing stacks for crafting.
 * 
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */
public class RecipeManager {
	private List<IRecipe> defaultRecipes;
	private List<RecipeItem> orderedRecipes;
	private List<ICraftingHandler> customCraftingHandlers = Lists.newArrayList();
	private static RecipeManager instance;
	private static boolean DEBUG_MODE = ProjectBench.DEBUG_MODE_ENABLED;
	private RecipeCrafter crafter = new RecipeCrafter();
	
	public RecipeManager(){
		initiateRecipeManager();
		defaultRecipes = null;
		instance = this;
		System.out.println("\tRecipe Manager active.");
	}
		
	public void initiateRecipeManager() {
		defaultRecipes = CraftingManager.getInstance().getRecipeList();
		associateRecipes();
		Collections.sort(orderedRecipes, new PBRecipeSorter());
		verifyList();
		indexList();
		if(ProjectBench.VERBOSE)
			displayList();
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
			if(rec == null)
				continue;
			potentialRecipe = translateRecipe(rec);
			if(potentialRecipe != null){
				if(!checkForRecipe(potentialRecipe))
					orderedRecipes.add(potentialRecipe);
			}
		}
	}
	
	/**
	 * Multi-level verification of a RecipeItem. Builds a new 
	 * list of RecipeItems, eliminating items with null recipes,
	 * invalid recipes, invalid results, and eliminating invalid
	 * alternatives.
	 */
	private void verifyList(){
		ArrayList<RecipeItem> goodList = new ArrayList<RecipeItem>();
		
		for(RecipeItem recipe : orderedRecipes){
			if(recipe.result == null)
				continue;
			if(!verifyAlternatives(recipe))
				continue;
			goodList.add(recipe);
		}
		orderedRecipes = new ArrayList<RecipeItem>();
		orderedRecipes.addAll(goodList);
	}
	
	private boolean verifyAlternatives(RecipeItem recipe){
		if(recipe.alternatives.size() == 0)
			return false;
		ArrayList<ItemStack[]> goodStacks = new ArrayList<ItemStack[]>();
		arrays : for(ItemStack[] isa : recipe.alternatives){
			if(isa == null || isa.length == 0)
				continue;
			for(ItemStack is : isa){
				if(is == null)
					continue arrays;
			}
			goodStacks.add(isa);
		}
		if(goodStacks.size() > 0){
			recipe.alternatives = new ArrayList<ItemStack[]>();
			for(ItemStack[] isa : goodStacks)
				recipe.alternatives.add(isa);
			return true;
		}else
			return false;
	}
	
	public ArrayList<RecipeItem> getDisabledRecipes(){
		ArrayList<RecipeItem> ls = new ArrayList();
		for(RecipeItem rec : orderedRecipes)
			if(!rec.isEnabled())
				ls.add(rec);
		return ls;
	}
	
	public boolean checkForRecipe(RecipeItem rec){
		if(rec.result.itemID == 5 && rec.result.getItemDamage() == 2)
			System.out.println("check");
		RecipeItem dup = null;
		ItemStack result = rec.result();
		int indexInList = 0;
		for(; indexInList < orderedRecipes.size(); indexInList++){
			if(orderedRecipes.get(indexInList) != null && RecipeCrafter.checkItemMatch(orderedRecipes.get(indexInList).result, result, false) && orderedRecipes.get(indexInList).result.stackSize == result.stackSize){
				dup = orderedRecipes.get(indexInList);
				break;
			}
		}
		if(dup != null){
//			if(dup.items != null){
//				dup.alternatives.add(dup.items());
//			}
			rec.consolidateStacks();
			for(ItemStack[] isa : dup.alternatives){
				for(ItemStack[] isa2 : rec.alternatives){
					boolean flag = true;
					if(isa.length == isa2.length){
						for(int i = 0; i < isa.length; i++){
							if(!RecipeCrafter.checkItemMatch(isa[i], isa2[i], false)){
								flag = false;
								break;
							}
						}
					}
					if(flag)
						return true;
				}
			}
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
	public RecipeItem searchForRecipe(ItemStack result, boolean truth){
		RecipeItem ri = new RecipeItem();
		ri.setResult(result);
		int i = Collections.binarySearch(orderedRecipes, ri, new PBRecipeSorter());
		if(i == -1 || i >= orderedRecipes.size() || i < 0){
			print("Recipe not found for " +result);
			return null;
		}
		RecipeItem ri2 = orderedRecipes.get(i);
		if(OreDictionary.itemMatches(ri2.result(), result, false) && (ri2.isEnabled() || truth))
			return orderedRecipes.get(i);
		else
			return null;
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
	
	public ArrayList<ItemStack> getRecipeItemsForPlan(ItemStack thePlan){
		if(thePlan != null){
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();
			NBTTagList tagList = thePlan.stackTagCompound.getTagList("Components");
			for(int i = 0; i < tagList.tagCount(); i++){
				NBTTagCompound stackTag = (NBTTagCompound)tagList.tagAt(i);
				if(stackTag.hasKey("id")){
					ItemStack stack = ItemStack.loadItemStackFromNBT(stackTag);
					list.add(stack);
				}
				else{
					list.add(null);
				}
			}
			return list;
		}
		return null;
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
		RecipeItem ri = searchForRecipe(stack, false);
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
		ArrayList<ItemStack> validRecipes = new ArrayList<ItemStack>();
		ArrayList<ItemStack[]> stacksForRecipe = null;
		boolean hasMeta = false;
		boolean flag = true;
		for(RecipeItem rec : orderedRecipes){
			flag = true;
			stacksForRecipe = rec.alternatives();
			hasMeta = rec.hasMeta();
			multiRecipeLoop : for(ItemStack[] recItems : stacksForRecipe){
				for(int i = 0; i < recItems.length; i++){
					for(ItemStack stackInInventory : stacks){
						if(stackInInventory != null){
							if(crafter.checkItemMatch(recItems[i], stackInInventory, false)){
								if(!crafter.checkItemMatch(recItems[i], stackInInventory, false))
	    							continue;
								//TODO container item
								if(recItems[i].getItem().hasContainerItem()){
									ItemStack contItem = recItems[i].getItem().getContainerItemStack(recItems[i]);
									if(contItem.isItemStackDamageable()){
										if(contItem.getItemDamage() + 1 <= contItem.getMaxDamage())
											recItems[i].stackSize = 0;
										else{
											recItems[i].setItemDamage(recItems[i].getItemDamage() + 1);
										}
									}else{
										if(recItems[i].stackSize <= stackInInventory.stackSize){
											recItems[i].stackSize = 0;
										}
									}
								}else if(recItems[i].stackSize <= stackInInventory.stackSize){
									recItems[i].stackSize = 0;
								}else if(recItems[i].stackSize > stackInInventory.stackSize){
									continue multiRecipeLoop;
								}
							}else if(recItems[i].getItemDamage() == OreDictionary.WILDCARD_VALUE){
								int id = OreDictionary.getOreID(recItems[i]);
								int id2 = OreDictionary.getOreID(stackInInventory);
								if(!(id == -1 || id != id2)){
									if(recItems[i].stackSize <= stackInInventory.stackSize){
										recItems[i].stackSize = 0;
									}else if(recItems[i].stackSize > stackInInventory.stackSize){
										continue multiRecipeLoop;
									}
								}
							}
						}
					}
					if(recItems[i].stackSize != 0){
						flag = false;
						break;
					}
				}
				if(flag && rec.isEnabled())
					validRecipes.add(rec.result());
			}
		}
		return validRecipes;
	}
	public HashMap<ItemStack, ItemStack[]> getPossibleRecipesMap(ItemStack[] stacks){
		HashMap<ItemStack, ItemStack[]> outputInputMap = new HashMap();
		ArrayList<ItemStack> validRecipes = new ArrayList<ItemStack>();
		ArrayList<ItemStack[]> stacksForRecipe = null;
		int id3 = 0;
		if(stacks != null && stacks.length == 1 && stacks[0] != null)
			 id3 = OreDictionary.getOreID(stacks[0]);
		boolean hasMeta = false;
		boolean flag = true;
		for(RecipeItem rec : orderedRecipes){
			flag = true;
			stacksForRecipe = rec.alternatives();
			hasMeta = rec.hasMeta();
			multiRecipeLoop : for(int index = 0; index < stacksForRecipe.size(); index++){
				ItemStack[] recItems = stacksForRecipe.get(index);
				for(int i = 0; i < recItems.length; i++){
					for(ItemStack stackInInventory : stacks){
						if(stackInInventory != null){
							if(RecipeCrafter.checkItemMatch(recItems[i], stackInInventory, false)){
								if(recItems[i].getItem().hasContainerItem()){
									ItemStack contItem = recItems[i].getItem().getContainerItemStack(recItems[i]);
									if(contItem.isItemStackDamageable()){
										if(contItem.getItemDamage() + 1 <= contItem.getMaxDamage())
											recItems[i].stackSize = 0;
										else{
											recItems[i].setItemDamage(recItems[i].getItemDamage() + 1);
										}
									}else{
										if(recItems[i].stackSize <= stackInInventory.stackSize){
											recItems[i].stackSize = 0;
										}
									}
								}else if(recItems[i].stackSize <= stackInInventory.stackSize){
									recItems[i].stackSize = 0;
								}else if(recItems[i].stackSize > stackInInventory.stackSize){
									continue multiRecipeLoop;
								}
							}else if(recItems[i].getItemDamage() == OreDictionary.WILDCARD_VALUE){
								int id = OreDictionary.getOreID(recItems[i]);
								int id2 = OreDictionary.getOreID(stackInInventory);
								if(!(id == -1 || id != id2)){
									if(recItems[i].stackSize <= stackInInventory.stackSize){
										recItems[i].stackSize = 0;
									}else if(recItems[i].stackSize > stackInInventory.stackSize){
										continue multiRecipeLoop;
									}
								}
							}
						}
					}
					if(recItems[i].stackSize != 0){
						flag = false;
						break;
					}
				}
				if(flag && rec.isEnabled()){
					outputInputMap.put(rec.result(), rec.alternatives.get(index));
				}
			}
		}
		return outputInputMap;
		
	}

	/**
	 * This method takes the format of the IRecipes
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
			ItemStack recOutput = (rec != null) ? rec.getRecipeOutput() : null;
			if(rec != null && rec.getRecipeOutput() != null && rec.getRecipeOutput().itemID == 30184 /*+256*/){
//				System.out.println("Check");
			}
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
				Object[] objArray = ((ShapedOreRecipe) rec).getInput();
				newRecItem.items = new ItemStack[objArray.length];
				for(int i = 0; i < objArray.length; i++){
					if(objArray[i] instanceof ArrayList){
						List theList = ((List)objArray[i]);
						if(((List)objArray[i]) != null && theList.size() > 0){
							newRecItem.items[i] = (ItemStack)theList.get(0);
							if(theList.size() > 1 && !newRecItem.oreDictItems.containsKey(theList.get(0))){
								ItemStack[] others = new ItemStack[theList.size() - 1];
								for(int j = 1; j < theList.size(); j++){
									if(theList.get(j) != null && theList.get(j) instanceof ItemStack){
										others[j-1] = (ItemStack)theList.get(j);
									}	
								}
								newRecItem.oreDictItems.put((ItemStack)theList.get(0), others);
							}
						}else{
							//Recipe is missing nonexistant ore types
							System.out.println("[Project Bench] Recipe Manager encountered a null OreDict type for item " +rec.getRecipeOutput());
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
				type = "CustomRecipe";
				newRecItem.setResult(rec.getRecipeOutput());
				if(rec != null && rec.getRecipeOutput() != null && rec.getRecipeOutput().itemID == 5 && rec.getRecipeOutput().getItemDamage() == 1){
					System.out.println("Check");
				}
				try{
					int i = 0;
					do{
						try
				        {
				            Field f = rec.getClass().getDeclaredFields()[i++];
				            f.setAccessible(true);
				            Object obj = f.get(rec);
				            if(obj != null){
				            	if(obj instanceof Object[]){
				            		Object[] objArr = (Object[])obj;
				            		newRecItem.input = new Object[objArr.length];
				            		for(int j = 0; j < objArr.length; j++){
				            			Object obj2 = objArr[j];
				            			newRecItem.input[j] = obj2;
				            			if(obj2 == null){
				            				if(newRecItem.items == null)
				            					newRecItem.items = new ItemStack[objArr.length];
				            			}else if(obj2 instanceof ItemStack){
				            				if(newRecItem.items == null)
				            					newRecItem.items = new ItemStack[objArr.length];
				            				newRecItem.items[j] = ((ItemStack)obj2).copy();
				            			}else if(obj2 instanceof ArrayList){
				            				ArrayList oreArrayList = (ArrayList)obj2;
				            				if(oreArrayList.get(0) != null && oreArrayList.get(0) instanceof ItemStack){
				                				if(newRecItem.items == null)
				                					newRecItem.items = new ItemStack[objArr.length];
				                				ItemStack oreDictStack = ((ItemStack)oreArrayList.get(0)).copy();
				                				newRecItem.items[j] = oreDictStack/*new ItemStack(oreDictStack.itemID, oreDictStack.stackSize, OreDictionary.WILDCARD_VALUE)*/;
				            				}
				            			}else if(obj2 instanceof String){
				            				ItemStack oreStack = OreDictionary.getOres((String)obj2).get(0);
				            				ItemStack newStack = oreStack /*new ItemStack(oreStack.itemID, oreStack.stackSize, OreDictionary.WILDCARD_VALUE)*/;
				            				if(newRecItem.items == null)
				            					newRecItem.items = new ItemStack[objArr.length];
				            				newRecItem.items[j] = newStack;
				            			}else{
				            				System.out.println("ProjectBench: Unaccounted for object type, disabling recipe. " +obj2.getClass());
				            				newRecItem.forceDisable();
				            			}
				            				
				            		}
				            	}
				            }
				        }
				        catch(ArrayIndexOutOfBoundsException ex)
				        {
				        	break;
				        }
						catch(Exception ex){
							newRecItem.forceDisable();
						}
					}
					while(true);
				}catch(Exception ex){
					newRecItem.forceDisable();
				}
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
			if(rec instanceof RecipesArmor || rec instanceof RecipesArmorDyes || rec instanceof RecipeFireworks || rec instanceof RecipesMapCloning)
				return null;
			System.err.println("Project Bench: Error encountered while translating recipe.");
			System.err.println("\t Recipe: " +rec);
			System.err.println("\t Recipe for: " +rec.getRecipeOutput());
			System.err.println("\t Recipe type: "+type);
			System.err.println("Please report this on the forums or GitHub.");
			return null;
		}
	}
	/**
	 * A custom class to interface with the recipes in Minecraft.
	 * Provides for much easier access of required items, output,
	 * metadata and more. 
	 * 
	 * @author bau5
	 *
	 */
	public class RecipeItem{
		private ItemStack[] items = null;
		private HashMap<ItemStack, ItemStack[]> oreDictItems = new HashMap<ItemStack, ItemStack[]>();
		private ArrayList<ItemStack[]> alternatives = new ArrayList<ItemStack[]>();
		private Object[] input;
		private IRecipe recipe;
		private ItemStack result;
		private int indexInList;
		private boolean isMetadataSensitive = false;
		private boolean usable = true;
		private String type = "[null]";
				
		public RecipeItem() { }
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
		
		private void consolidateStacks(){
			if(items == null)
				return;
			for(ItemStack stack : items){
				if(stack == null){
					continue;
				}else if(stack.stackSize > 1 || stack.stackSize < 1)
					stack.stackSize = 1;
			}
			
			alternatives.add(new RecipeCrafter().consolidateItemStacks(items));
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
		
		public boolean isEnabled(){
			return usable;
		}
		public void forceEnable(){
			usable = true;
		}
		public void forceDisable(){
			usable = false;
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
		
		@Override
		public String toString()
	    {
	        return result + ":" + type +"x" + ((alternatives != null) ? alternatives.size() : 0);
	    }
		
	}
	public static RecipeManager instance(){
		return instance;
	}
	public static void print(String message){
		if(DEBUG_MODE /*|| ProjectBench.DEV_ENV*/)
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