package bau5.mods.projectbench.common.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.projectbench.common.tileentity.ContainerProjectBench;
import bau5.mods.projectbench.common.tileentity.TEProjectBenchII;
import bau5.mods.projectbench.common.tileentity.TileEntityProjectBench;

import com.google.common.collect.Lists;

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
	private TileEntityProjectBench tpbRefI = null;
	
	
	public static boolean checkItemMatch(ItemStack target, ItemStack input, boolean stackSize){
		if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
		if(target.itemID == input.itemID){
			return (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage()/* ||(OreDictionary.getOreID(target) != -1 && OreDictionary.getOreID(target) != 0 && OreDictionary.getOreID(target) == OreDictionary.getOreID(input))*/) && (stackSize ? target.stackSize <= input.stackSize : true);
		}else{
			if(target.getItemDamage() == OreDictionary.WILDCARD_VALUE){
				int id = OreDictionary.getOreID(target);
				int id2 = OreDictionary.getOreID(input);
				if(id != -1 && id == id2){
					return /*true*/(stackSize ? target.stackSize <= input.stackSize : true);
				}
			}else {
				int id1 = OreDictionary.getOreID(target);
				int id2 = OreDictionary.getOreID(input);
				if(id1 == id2 && id1 != -1){
					if(id1 == 0)
						return (target.getItemDamage() == input.getItemDamage());
					return /*true*/(stackSize ? target.stackSize <= input.stackSize : true);
				}
			}
		}
		return false;
	}
	
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
					if(checkItemMatch(stackInList, stackInArray, false)){
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
		return orderItemStacksByID(stacks2);
	}
	
	public ItemStack[] makeShallowCopy(ItemStack[] stacks){
		ItemStack[] copy = new ItemStack[stacks.length];
		for(int i = 0; i < stacks.length; i++){
			if(stacks[i] == null)
				copy[i] = null;
			else
				copy[i] = ItemStack.copyItemStack(stacks[i]);
		}
		return copy;
	}
	
	public boolean checkListAgainstList(ItemStack[] arr1, ItemStack[] arr2){
		boolean flag = true;
		arr1 = makeShallowCopy(arr1);
		
		for(ItemStack stack : arr1){
			if(stack == null) continue;
			for(ItemStack stack2 : arr2){
				if(stack2 == null) continue;
				if(RecipeCrafter.checkItemMatch(stack, stack2, true)){
					if(stack2.stackSize >= stack.stackSize)
						stack.stackSize = 0;
					break;
				}
			}
		}
		for(ItemStack is : arr1){
			flag = (is.stackSize == 0);
			if(!flag)
				break;
		}
		return flag;
	}
	
	public ItemStack[] listToArray(ArrayList<ItemStack> list){
		ItemStack[] newArray = new ItemStack[list.size()];
		for(int i = 0; i < list.size(); i++){
			newArray[i] = list.get(i);
		}
		return newArray;
	}
	
	public ItemStack[] orderItemStacksByID(ItemStack[] originalArray){
		if(originalArray == null) return originalArray;
		if(originalArray.length == 0) return originalArray;
		ArrayList<ItemStack> stackList = Lists.newArrayList(originalArray);
		ItemStack[] sortedArray = new ItemStack[originalArray.length];
		Collections.sort(stackList, new PBRecipeSorter());
		for(int i = 0; i < sortedArray.length; i++)
			sortedArray[i] = stackList.get(i);
		return sortedArray;
	}

	public ItemStack[] getMissingStacks(ContainerProjectBench cpb, ItemStack thePlan){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		ItemStack is = cpb.tileEntity.getStackInSlot(27);
		ItemStack[] builtStacks = new ItemStack[9];
		if(is != null && ItemStack.areItemStacksEqual(is, thePlan) && ItemStack.areItemStackTagsEqual(is, thePlan)){
			ItemStack[] plansItems = listToArray(RecipeManager.instance().getRecipeItemsForPlan(thePlan));
			plansItems = consolidateItemStacks(plansItems);
			ItemStack[] supplyItems = consolidateItemStacks(cpb.tileEntity.getSupplyInventoryItems());
			pi : for(int i = 0; i < plansItems.length; i++){
				ItemStack missing = ItemStack.copyItemStack(plansItems[i]);
				for(int j = 0; j < supplyItems.length; j++){
					if(plansItems[i] != null && supplyItems[j] != null && checkItemMatch(plansItems[i], supplyItems[j], false)){
						if(plansItems[i].stackSize > supplyItems[j].stackSize){
							missing.stackSize = plansItems[i].stackSize - supplyItems[j].stackSize;
							list.add(missing);
						}else{
							continue pi;
						}
					}
				}
				list.add(missing);
			}
		}
		if(list.size() > 0)
			return listToArray(list);
		else
			return new ItemStack[0];
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
		for(int i = 0; i < tries; i++){
			main : for(ItemStack is : toConsume){
				for(ItemStack stackInInventory : consolidatedStacks){
					if(is != null){
						if(checkItemMatch(is, stackInInventory, true)){
							//TODO container item
							boolean success = consumeItemStack(is);
							if(!success){
								flag = false;
								return numMade;
							}
							continue main;
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
				if(checkItemMatch(stack, stackInInventory, false)){
					if(stackInInventory.getItem().hasContainerItem()){
						ItemStack contItem = stackInInventory.getItem().getContainerItemStack(stackInInventory);
						if(contItem != null){
							if(contItem.isItemStackDamageable()){
								contItem.setItemDamage(contItem.getItemDamage() - 1);
								stack.stackSize = 0;
							}else{
								decreaseStackSize(i, stack.stackSize);
								stack.stackSize = 0;
								addStackToInventory(contItem);
							}
						}
					}else if(stack.stackSize <= stackInInventory.stackSize){
						decreaseStackSize(i, stack.stackSize);
						stack.stackSize = 0;
					}/*else{
						return false;
					}*/
					else{
						stack.stackSize -= stackInInventory.stackSize;
						decreaseStackSize(i, stackInInventory.stackSize);
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
				else if(tpbRefI != null)
					tpbRefI.setInventorySlotContents(index +tpbRefI.getSupplyMatrixStart(), null);
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
					if(theStack.stackSize + stack.stackSize <= theStack.getMaxStackSize())
						theStack.stackSize += stack.stackSize;
					else
						tpbRef.setInventorySlotContents(i, stack);
					return true;
				}
			}
		}else if(tpbRefI != null){
			for(int i = tpbRefI.getSupplyMatrixStart(); i < (tpbRefI.getSupplyMatrixStart() + tpbRefI.getSupplyMatrixSize()); i++){
				theStack = tpbRefI.getStackInSlot(i);
				if(theStack == null || (OreDictionary.itemMatches(theStack, stack, false) && (theStack.stackSize + stack.stackSize <= theStack.getMaxStackSize()))){
					if(theStack != null){
						if(theStack.stackSize + stack.stackSize <= theStack.getMaxStackSize())
							theStack.stackSize += stack.stackSize;
					}else
						tpbRefI.setInventorySlotContents(i, stack);
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
				if(checkItemMatch(stack, sin, false)){
					if(stack.stackSize <= sin.stackSize){
						stack.stackSize = 0;
					}else{
						int stackSize = sin.stackSize;
						stack.stackSize -= stackSize;
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
	
	public void onItemCrafted(ItemStack stack, World worldObj, EntityPlayer player, int amountCrafted){
		stack.onCrafting(worldObj, player, amountCrafted);
	}
	
	public void addInventoryReference(ItemStack[] ref){
		sourceInventory = ref;
	}
	
	public void addTPBReference(TEProjectBenchII ref){
		tpbRef = ref;
	}
	
	public void addTPBIReference(TileEntityProjectBench ref){
		tpbRefI = ref;
	}
}
