package bau5.mods.projectbench.common.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.recipes.RecipeCrafter;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * ContainerProjectBenchII
 * 
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class ContainerProjectBenchII extends Container	
{
	protected TEProjectBenchII tileEntity;
	
	private boolean postSlotClick = false;
	
	public ContainerProjectBenchII(InventoryPlayer invPlayer, TEProjectBenchII tpbII){
		tileEntity = tpbII;
		layoutContainer();
		bindPlayerInventory(invPlayer);
		detectAndSendChanges();
		if(tileEntity.worldObj.isRemote){
			updateAll();}
	}
	public void updateAll(){
		lookForOutputs();
		tileEntity.setRecipeMap(RecipeManager.instance().getPossibleRecipesMap(tileEntity.consolidateItemStacks(false)));
	
	}
	public void lookForOutputs(){
		ItemStack[] stacks = tileEntity.consolidateItemStacks(true);
		tileEntity.setListForDisplay(RecipeManager.instance().getValidRecipesByStacks(stacks));
	}
	@SideOnly(Side.CLIENT)
	public void updateToolTipMap(){
		ItemStack[] stacks = tileEntity.consolidateItemStacks(false);
		tileEntity.setRecipeMap(RecipeManager.instance().getPossibleRecipesMap(stacks));	
	}
	public ItemStack[] getStacksToConsumeForSlot(int slotIndex){
		return tileEntity.getStacksForResult(tileEntity.getStackInSlot(slotIndex));
	}
	
	private void layoutContainer(){
		int row, col, index = -1;
		Slot slot = null;
		
		//Possible Recipes Matrix
		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 9; col++)
			{	
				addSlotToContainer(new SlotPBII(tileEntity, ++index, 5 + col * 18, 16 + row * 18));
			}
		}
		
		//Supply Matrix
		for(row = 0; row < 2; row++)
		{
			for(col = 0; col < 9; col++)
			{
				if(row == 1)
				{
					slot = new Slot(tileEntity, ++index, 5 + col * 18, 
									(row * 2 - 1) + 76 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(tileEntity, ++index, 5 + col * 18,
							76 + row * 18);
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
											5 + j * 18, 84 + i * 18 + 36));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 5 + i * 18, 142 + 36));
		}
	}
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return tileEntity.isUseableByPlayer(player);
	}
	@Override
	public ItemStack slotClick(int slot, int clickType, int meta, EntityPlayer player){
		int fake = clickType;
		ItemStack originalStack = (slot < 45 && slot >= 0) ? tileEntity.getStackInSlot(slot) : null;
		if(meta == 6)
			meta = 0;
		if(slot < 27 && slot >= 0){
			if(player.worldObj.isRemote){
				if(clickType == 1 || clickType == 2)
					return handleSlotClick(slot, fake, meta, originalStack, player);
				if(originalStack == null)
					return null;
				ItemStack returnStack = handleSlotClick(slot, fake, meta, originalStack, player);
				if(returnStack != null){
					ItemStack stackOnMouse = player.inventory.getItemStack();
					if(stackOnMouse == null){
						player.inventory.setItemStack(ItemStack.copyItemStack(returnStack));
					}
					else{
						if(stackOnMouse.isItemEqual(returnStack)){
							if(stackOnMouse.stackSize + returnStack.stackSize <= stackOnMouse.getMaxStackSize())
								stackOnMouse.stackSize += returnStack.stackSize;
							else
								return null;
						}
					}
					return returnStack;
				}else{
					if(player.worldObj.isRemote)
						lookForOutputs();
					return null;
				}
			}else{
				return null;
			}
		}else{
			if(tileEntity.worldObj.isRemote)
				updateAll();
			ItemStack stack = super.slotClick(slot, fake, meta, player);		
			return stack;
		}
	}

	public ItemStack serverMouseClick(int slot, int clickType, int meta, EntityPlayer ep, ItemStack stackRequested) {
		if(clickType != 1 && clickType != 2){
			if(slot >= 0 && slot < 27){
				int fake = (meta == 6) ? 0 : meta;
				ItemStack returnStack = handleSlotClick(slot, clickType, meta, stackRequested, ep);
				if(returnStack != null){
					ItemStack stackOnMouse = ep.inventory.getItemStack();
					if(stackOnMouse == null){
						ep.inventory.setItemStack(ItemStack.copyItemStack(returnStack));
					}
					else{
						if(stackOnMouse.isItemEqual(returnStack)){
							if(stackOnMouse.stackSize + returnStack.stackSize <= stackOnMouse.getMaxStackSize())
								stackOnMouse.stackSize += returnStack.stackSize;
						}
					}
					return returnStack;
				}
			}
		}
		return null;
	}
	
	public ItemStack getItemStackFromTileEntity(int slot){
		return tileEntity.getStackInSlot(slot);
	}
	
	private ItemStack handleSlotClick(int slot, int clickType, int clickMeta, ItemStack stackInSlot, EntityPlayer player) {
		if(clickType == 1){
			tileEntity.removeResultFromDisplay(stackInSlot);
			return null;
		}else if(clickType == 2){
			return null;
		}else{
			ItemStack stackOnMouse = player.inventory.getItemStack();
			ArrayList<ItemStack[]> items = null;
			if(stackInSlot == null || stackInSlot.stackSize <= 0)
				return null;
			if(stackOnMouse != null){
				if(stackOnMouse.isItemEqual(stackInSlot)){
					if(stackOnMouse.stackSize + stackInSlot.stackSize <= stackOnMouse.getMaxStackSize()){
						items = RecipeManager.instance().getComponentsToConsume(stackInSlot);
					}
				}
			}else{
				items = RecipeManager.instance().getComponentsToConsume(stackInSlot);
			}
			if(items == null){
				return null;
			}
			boolean success = false;
			int numMade = 0;
			for(ItemStack[] isa : items){
				numMade = tileEntity.consumeItems(isa, stackInSlot, (clickMeta == 1));
				success = (numMade != 0);
				if(success)
					break;
				else
					continue;
			}
			if(success){
				stackInSlot.stackSize *= numMade;
				new RecipeCrafter().onItemCrafted(stackInSlot, player.worldObj, player, numMade);
				return stackInSlot;
			}else{
				lookForOutputs();
				return null;
			}
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
                if (!this.mergeItemStack(stack2, 45, 81, false))
                {
                    return null;
                }
                postSlotClick = true;
            }
            //Merge player inventory item with supply matrix
            else if (numSlot >= 45 && numSlot <= 80)
            {
                if (!this.mergeItemStack(stack2, 27, 44, false))
                {
                    return null;
                }
                postSlotClick = true;
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
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
		tileEntity.initSlots = true;
		super.putStacksInSlots(par1ArrayOfItemStack);
		tileEntity.initSlots = false;
		tileEntity.updateOutputRecipes();
	}
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		if(postSlotClick && tileEntity.worldObj.isRemote){
			lookForOutputs();
			postSlotClick = false;
		}
	}

	public void scrambleMatrix() {
		tileEntity.scrambleMatrix();
	}
}
