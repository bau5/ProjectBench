package bau5.mods.projectbench.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.recipes.RecipeManager;

public class ContainerProjectBenchII extends Container	
{
	protected TEProjectBenchII tileEntity;
	
	private boolean slotClicked = false;
	
	public ContainerProjectBenchII(InventoryPlayer invPlayer, TEProjectBenchII tpbII){
		tileEntity = tpbII;
		layoutContainer();
		bindPlayerInventory(invPlayer);
		detectAndSendChanges();
		lookForOutputs();
	}

	public void lookForOutputs(){
		ItemStack[] stacks = tileEntity.consolidateItemStacks(true);
		tileEntity.setListForDisplay((ArrayList)RecipeManager.instance().getValidRecipesByStacks(stacks));
	}
	
	private void layoutContainer(){
		int row, col, index = -1;
		Slot slot = null;
		
		//Possible Recipes Matrix
		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 9; col++)
			{	
				addSlotToContainer(new SlotPBII(tileEntity, ++index, 8 + col * 18, 14 + row * 18));
				
			}
		}
		
		//Supply Matrix
		for(row = 0; row < 2; row++)
		{
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, ++index, 8 + col * 18, 
									(row * 2 - 1) + 74 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, ++index, 8 + col * 18,
							74 + row * 18);
					addSlotToContainer(slot);
				}
			}
		}
	}
	
	private void bindPlayerInventory(InventoryPlayer invPlayer){
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
											8 + j * 18, 84 + i * 18 + 34));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 34));
		}
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return tileEntity.isUseableByPlayer(player);
	}
	@Override
	public ItemStack slotClick(int slot, int clickType, int par3, EntityPlayer player){
		int fake = clickType;
		System.out.println(par3);
		ItemStack originalStack = (slot < 45 && slot >= 0) ? tileEntity.getStackInSlot(slot) : null;
		handleSlotClick(slot, fake, originalStack, player);
		if((clickType == 1 || clickType == 2) && (slot < 27 && slot >= 0))
			return null;
		if(par3 == 6)
			fake = 0;
		ItemStack stack = super.slotClick(slot, fake, par3, player);		

		return stack;
		
	}
	private boolean handleSlotClick(int slot, int clickType, ItemStack stackInSlot, EntityPlayer player) {
		if(slot < 0)
			return false;

		if(slot < 27){
			if(clickType == 1){
				tileEntity.removeResultFromDisplay(stackInSlot);
				
				return false;
			}else if(clickType == 2){
					tileEntity.scrambleMatrix();
				
				return false;
			}else{
				ItemStack stackOnMouse = player.inventory.getItemStack();
				ItemStack[] items = null;
				if(stackInSlot == null || stackInSlot.stackSize <= 0)
					return false;
				if(stackOnMouse != null){
					if(ItemStack.areItemStacksEqual(stackOnMouse, stackInSlot))
						if(stackOnMouse.stackSize + stackInSlot.stackSize <= stackOnMouse.getMaxStackSize())
							items = RecipeManager.instance().getComponentsToConsume(stackInSlot);
				}else
					items = RecipeManager.instance().getComponentsToConsume(stackInSlot);
				if(items == null)
					return false;
				boolean success = tileEntity.consumeItems(items);
				if(success){
					return true;
				}else{
					lookForOutputs();
				}
				return false;
			}
		}else{
			if(slot < 45){
				slotClicked = true;
				lookForOutputs();
			}
			return true;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            //Merge crafting matrix item with supply matrix inventory
            if(numSlot >= 0 && numSlot < 27)
            {
            	if(!this.mergeItemStack(stack2, 45, 80, true))
            	{
            		return null;
            	}
            }
            //Merge Supply matrix item with player inventory
            else if (numSlot >= 27 && numSlot < 45)
            {
                if (!this.mergeItemStack(stack2, 45, 80, true))
                {
                    return null;
                }
            }
            //Merge player inventory item with supply matrix
            else if (numSlot >= 45 && numSlot <= 80)
            {
                if (!this.mergeItemStack(stack2, 27, 44, false))
                {
                    return null;
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
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
	}
}
