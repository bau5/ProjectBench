package bau5.mods.projectbench.common.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.projectbench.common.TEProjectBenchII;

/**
 * RecipeCrafter
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class RecipeCrafter {	
	
	private ItemStack[] sourceInventory = null;
	private TEProjectBenchII tpbRef = null;
	
	public ItemStack[] consolidateItemStacks(ItemStack[] stacks){
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack stack = null;
		for(int i = 0; i < stacks.length; i++){
			stack = null;
			if(stacks[i] != null){
				stack = stacks[i];
			}
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
						if(stackInList.getItem().getContainerItem() != null){
							consolidatedItems.add(stackInArray.copy());
						}else
							stackInList.stackSize += stackInArray.stackSize;
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

	public int consumeItems(ItemStack[] toConsume, ItemStack[] supplies, ItemStack result, boolean max) {
		int numMade = 0;
		ItemStack resultStack = ItemStack.copyItemStack(result);
		if(!checkForItems(toConsume.clone(), supplies)){
			return numMade;
		}
		int tries = (max) ? (resultStack.getMaxStackSize()/resultStack.stackSize) : 1;
		while(tries * resultStack.stackSize > resultStack.getMaxStackSize())
			tries--;
		ItemStack[] consolidatedStacks = supplies;
		boolean flag = true;
		counterLoop : for(int i = 0; i < tries; i++){
			main : for(ItemStack is : toConsume){
				for(ItemStack stackInInventory : consolidatedStacks){
					if(is != null){
						if(OreDictionary.itemMatches(is, stackInInventory, false)){
							//TODO container item
							if(stackInInventory.stackSize < is.stackSize && is.getItem().getContainerItem() == null){
								flag = false;
								continue;
							}
							boolean success = consumeItemStack(is);
							if(!success){
								flag = false;
								continue;
							}
							if(stackInInventory.getItem().getContainerItem() == null)
								stackInInventory.stackSize -= is.stackSize;
							continue main;
						}else if(is.getItemDamage() == OreDictionary.WILDCARD_VALUE){
							int id = OreDictionary.getOreID(is);
							int id2 = OreDictionary.getOreID(stackInInventory);
							if(!(id == -1 || id != id2)){
								if(stackInInventory.stackSize <= is.stackSize){
									boolean success = consumeItemStack(is);
									if(!success){
										flag = false;
										continue;
									}
									if(stackInInventory.getItem().getContainerItem() == null)
										stackInInventory.stackSize -= is.stackSize;
									continue main;
								}else if(stackInInventory.stackSize > is.stackSize){
									break counterLoop;
								}
							}
						}
					}
				}
			}
			if(flag){
				numMade++;
			}
		}return numMade;
	}
	public boolean consumeItemStack(ItemStack toConsume){
		ItemStack stack = toConsume.copy();
		ItemStack stackInInventory = null;
		for(int i = 0; i < sourceInventory.length; i++){
			stackInInventory = sourceInventory[i];
			if(stackInInventory == null){
				continue;
			}else{
				if(OreDictionary.itemMatches(stack, stackInInventory, false)){
					if(stack.stackSize <= stackInInventory.stackSize){
						decreaseStackSize(i, stack.stackSize);
						stack.stackSize = 0;
					}else{
						int stackSize = stackInInventory.stackSize;
						decreaseStackSize(i, stackSize);
						stack.stackSize -= stackSize;
					}
				}else if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
					int id = OreDictionary.getOreID(stack);
					int id2 = OreDictionary.getOreID(stackInInventory);
					if(!(id == -1 || id != id2)){
						if(stack.stackSize <= stackInInventory.stackSize){
							decreaseStackSize(i, stack.stackSize);
							stack.stackSize = 0;
						}else{
							int stackSize = stackInInventory.stackSize;
							decreaseStackSize(i, stackSize);
							stack.stackSize -= stackSize;
						}
					}
				}
			}
			if(stack.stackSize == 0)
				break;
		}
		return (stack.stackSize == 0);
	}
	
	public ItemStack decreaseStackSize(int index, int amount){
		if(sourceInventory != null){
			sourceInventory[index].stackSize -= amount;
			if(sourceInventory[index].stackSize == 0){
				sourceInventory[index] = null;
				if(tpbRef != null)
					tpbRef.setInventorySlotContents(index +tpbRef.inventoryStart, null);
			}
			return sourceInventory[index];
		}
		return null;
	}
	
	public boolean addStackToInventory(ItemStack stack){
		ItemStack theStack = null;
		if(tpbRef != null){
			for(int i = tpbRef.inventoryStart; i < (tpbRef.inventoryStart + tpbRef.supplyMatrixSize); i++){
				theStack = tpbRef.getStackInSlot(i);
				if(theStack == null || (OreDictionary.itemMatches(theStack, stack, false) && (theStack.stackSize + stack.stackSize <= theStack.getMaxStackSize()))){
					if(theStack != null)
						theStack.stackSize += stack.stackSize;
					else
						tpbRef.setInventorySlotContents(i, stack);
					return true;
				}
			}
		}else if(sourceInventory != null){
			for(int i = 0; i < sourceInventory.length; i++){
				theStack = sourceInventory[i];
				if(theStack == null || (OreDictionary.itemMatches(theStack, stack, false) && (theStack.stackSize + stack.stackSize <= theStack.getMaxStackSize()))){
					if(theStack != null)
						theStack.stackSize += stack.stackSize;
					else
						sourceInventory[i] = stack.copy();
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkForItems(ItemStack[] items, ItemStack[] arrToConsume) {
		ItemStack[] consolidatedStacks = arrToConsume;
		ItemStack[] clonedItems = new ItemStack[items.length];
		for(int i = 0; i < clonedItems.length; i++){
			clonedItems[i] = ItemStack.copyItemStack(items[i]);
		}
		ItemStack stack = null;
		for(int i = 0; i < items.length; i++){
			stack = clonedItems[i];
			for(ItemStack sin : consolidatedStacks){
				if(OreDictionary.itemMatches(stack, sin, false)){
					if(stack.stackSize <= sin.stackSize){
						stack.stackSize = 0;
					}else{
						int stackSize = sin.stackSize;
						stack.stackSize -= stackSize;
					}
				}else if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
					int id = OreDictionary.getOreID(stack);
					int id2 = OreDictionary.getOreID(sin);
					if(!(id == -1 || id != id2)){
						if(stack.stackSize <= sin.stackSize){
							decreaseStackSize(i, stack.stackSize);
							stack.stackSize = 0;
						}else{
							int stackSize = sin.stackSize;
							decreaseStackSize(i, stackSize);
							stack.stackSize -= stackSize;
						}
					}
				}
			}
		}
		for(ItemStack toCheck : clonedItems){
			if(toCheck.stackSize != 0)
				return false;
		}
		
		return true;
	}
	
	public void addInventoryReference(ItemStack[] ref){
		sourceInventory = ref;
	}
	
	public void addTPBReference(TEProjectBenchII ref){
		tpbRef = ref;
	}
	
}
