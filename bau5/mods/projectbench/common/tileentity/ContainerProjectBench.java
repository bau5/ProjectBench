package bau5.mods.projectbench.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.recipes.RecipeCrafter;
import bau5.mods.projectbench.common.recipes.RecipeManager;

/**
 * 
 * ContainerProjectBench
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class ContainerProjectBench extends Container
{
	public TileEntityProjectBench tileEntity;
	
	public IInventory craftSupplyMatrix;
	public int craftResultSlot = 0;
	public int planSlot = 27;
	private boolean containerChanged;
	private boolean netEditingContainer = false;
		
	public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		tileEntity = tpb;
		craftSupplyMatrix = tileEntity.craftSupplyMatrix;
		addSlotToContainer(new SlotPBCrafting(this, invPlayer.player, tileEntity, tileEntity.craftResult, 
										 tileEntity, craftResultSlot, 124, 35));
		layoutContainer(invPlayer, tileEntity);
		addSlotToContainer(new SlotPBPlan(tileEntity, planSlot, 9, 35));
		bindPlayerInventory(invPlayer);
		containerChanged = true;
		detectAndSendChanges();
	}
	private void layoutContainer(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		int row;
		int col;
		int index = -1;
		int counter = 0;
		Slot slot = null;

		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 3; col++)
			{
				slot = new Slot(tileEntity, ++index, 30 + col * 18, 17 + row * 18);
				addSlotToContainer(slot);
				counter++;
			}
		}
		
		for(row = 0; row < 2; row++)
		{
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, 18 + col, 8 + col * 18, 
									(row * 2 - 1) + 77 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, 9 + col, 8 + col * 18,
							77 + row * 18);
					addSlotToContainer(slot);
				}
				counter++;
			}
		}
	}
	protected void bindPlayerInventory(InventoryPlayer invPlayer) 
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
											8 + j * 18, 82 + i * 18 + 39));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 37));
		}
	}
	public void updateCrafting(boolean flag){
		tileEntity.markShouldUpdate();
	}
	
	public ItemStack getPlanStack(){
		return tileEntity.getPlanStack();
	}
	
	public ItemStack getPlanResult(){
		return tileEntity.getPlanResult();
	}

	public boolean validPlanInSlot(){
		return tileEntity.validPlanInSlot();
	}
	
	public void writePlanToNBT() {
		if(tileEntity.getResult() == null)
			return;
		NBTTagCompound mainTag = new NBTTagCompound("PlanData");
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < 9; i++){
			NBTTagCompound tag = new NBTTagCompound();
			ItemStack stack = tileEntity.getStackInSlot(i);
//			int oreid = OreDictionary.getOreID(stack);
//			ItemStack oreStack = (oreid != -1) ? ((OreDictionary.getOres(oreid).size() > 1) ? OreDictionary.getOres(oreid).get(1) :stack) : stack;
//			if(oreStack != null)
//				tag = oreStack.writeToNBT(tag);
			if(stack!=null)
				tag = stack.writeToNBT(tag);
			list.appendTag(tag);
		}
		mainTag.setTag("Components", list);
		mainTag.setTag("Result", tileEntity.getResult().writeToNBT(new NBTTagCompound()));
		getPlanStack().stackTagCompound = mainTag;
		getPlanStack().setItemDamage(1);
	}
	
	@Override
	public ItemStack slotClick(int slot, int clickType, int clickMeta, EntityPlayer player) {
		if(slot <= 9 && slot > -1 || slot == 28)
			updateCrafting(true);
		if(slot == 0 && validPlanInSlot() && ItemStack.areItemStacksEqual(getPlanResult(), tileEntity.getResult())){
			ItemStack stack = handleSlotClick(slot, clickType, clickMeta, player);
			if(stack == null){
				ItemStack result = tileEntity.findRecipe(false);
				if(result != null)
					return super.slotClick(slot, clickType, clickMeta, player);
			}
			return stack;
		}else
			return super.slotClick(slot, clickType, clickMeta, player);
	}
	
	public ItemStack handleSlotClick(int slot, int clickType, int clickMeta, EntityPlayer player){
		ItemStack returnStack = tileEntity.getResult();
		if(returnStack == null)
			return null;
		RecipeCrafter crafter = new RecipeCrafter();
		crafter.addTPBIReference(tileEntity);
		ItemStack[] supplyMatrix = tileEntity.getSupplyInventoryItems();
		crafter.addInventoryReference(supplyMatrix);
		
		int numMade = crafter.consumeItems(crafter.consolidateItemStacks(crafter.listToArray(RecipeManager.instance().getRecipeItemsForPlan(getPlanStack()))), crafter.consolidateItemStacks(supplyMatrix), getPlanResult(), clickMeta == 1);
		ItemStack outputClone = returnStack.copy();
		if(numMade == 0)
			return null;
		outputClone.stackSize *= numMade;
		ItemStack stackOnMouse = player.inventory.getItemStack();
		if(stackOnMouse == null){
			player.inventory.setItemStack(ItemStack.copyItemStack(outputClone));
		}
		else{
			if(stackOnMouse.isItemEqual(outputClone)){
				if(stackOnMouse.stackSize + outputClone.stackSize <= stackOnMouse.getMaxStackSize())
					stackOnMouse.stackSize += outputClone.stackSize;
				else
					return null;
			}
		}
		updateCrafting(true);
		return outputClone;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		tileEntity.containerInit = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		tileEntity.containerInit = false;
		tileEntity.onInventoryChanged();
	}
	@Override
	public void putStackInSlot(int slot, ItemStack itemStack) {
		tileEntity.containerInit = true;
		super.putStackInSlot(slot, itemStack);
		tileEntity.containerInit = false;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
        	boolean flag = false;
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            if(stack2.getItem().equals(ProjectBench.instance.pbPlan))
            	flag = this.mergeItemStack(stack2, 28, 29, false);
            if(!flag){
	            if (numSlot == 0)
	            {
	                if (!this.mergeItemStack(stack2, 10, 55, true))
	                {
	                    return null;
	                }
	                updateCrafting(true);
	            }
	            //Merge crafting matrix item with supply matrix inventory
	            else if(numSlot > 0 && numSlot <= 9)
	            {
	            	if(!this.mergeItemStack(stack2, 10, 28, false))
	            	{
	            		if(!this.mergeItemStack(stack2, 28, 64, false))
	            		{
	                		return null;
	            		}
	            	}
	            	updateCrafting(true);
	            }
	            //Merge Supply matrix item with player inventory
	            else if (numSlot >= 10 && numSlot <= 27)
	            {
	                if (!this.mergeItemStack(stack2, 29, 65, false))
	                {
	                    return null;
	                }
	            }
	            //Merge player inventory item with supply matrix
	            else if (numSlot >= 28 && numSlot < 64)
	            {
	                if (!this.mergeItemStack(stack2, 10, 28, false))
	                {
	                    return null;
	                }
	            }
            }

            if (stack2.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (stack2.stackSize == stack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, stack2);
        }

        return stack;
    }
}

