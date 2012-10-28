package bau5.mods.projectbench.common;

import net.minecraft.src.Container;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryCraftResult;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotCrafting;

public class ContainerProjectBench extends Container
{
	protected TileEntityProjectBench tileEntity;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3); 
	public IInventory craftResult = new InventoryCraftResult();
	public IInventory craftingSupplyMatrix = new InventoryBasic("pbCraftingSupply", 18);
	public int craftResultSlot = 0;
	
	public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		tileEntity = tpb;
		addSlotToContainer(new SlotPBCrafting(this, invPlayer.player, craftMatrix, craftResult, 
										 craftingSupplyMatrix, craftResultSlot, 124, 35));
		layoutContainer(invPlayer, tileEntity);
		bindPlayerInventory(invPlayer);
	}
	private void layoutContainer(InventoryPlayer invPlayer, TileEntityProjectBench tpb)
	{
		int row;
		int col;
		int index = -1;
		int counter = 0;
		Slot slot;

		for(row = 0; row < 3; row++)
		{
			for(col = 0; col < 3; col++)
			{
				slot = new Slot(craftMatrix, ++index, 30 + col * 18, 17 + row * 18);
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
					slot = new Slot(craftingSupplyMatrix, 9 + col, 8 + col * 18, 
									(row * 2 - 1) + 77 + row * 18);
					addSlotToContainer(slot);
				} else
				{
					slot = new Slot(craftingSupplyMatrix, row + col, 8 + col * 18,
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
											8 + j * 18, 84 + i * 18 +37));
			}
		}
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 37));
		}
	}
	public void onCraftMatrixChanged(IInventory inv)
	{
		ItemStack stack = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix);
		craftResult.setInventorySlotContents(0, stack);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return tileEntity.isUseableByPlayer(player);
	}
	public ItemStack transferStackInSlot(int numSlot)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();

            if (numSlot == 0)
            {
                if (!this.mergeItemStack(stack2, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(stack2, stack);
            }
            else if (numSlot >= 10 && numSlot < 37)
            {
                if (!this.mergeItemStack(stack2, 37, 46, false))
                {
                    return null;
                }
            }
            else if (numSlot >= 37 && numSlot < 46)
            {
                if (!this.mergeItemStack(stack2, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(stack2, 10, 46, false))
            {
                return null;
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

            slot.onPickupFromSlot(stack2);
        }

        return stack;
    }
//	@Override
//    public ItemStack transferStackInSlot(int slot) 
//	{
//        ItemStack stack = null;
//        Slot slotObject = (Slot) inventorySlots.get(slot);
//
//        //null checks and checks if the item can be stacked (maxStackSize > 1)
//        if (slotObject != null && slotObject.getHasStack()) 
//        {
//            ItemStack stackInSlot = slotObject.getStack();
//            stack = stackInSlot.copy();
//
//            //merges the item into player inventory since its in the tileEntity
//            if (slot == 0) {
//                if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
//                        return null;
//                }
//            //places it into the tileEntity is possible since its in the player inventory
//            } else if (!mergeItemStack(stackInSlot, 0, 1, false)) {
//                return null;
//            }
//
//            if (stackInSlot.stackSize == 0) {
//                slotObject.putStack(null);
//            } else {
//                slotObject.onSlotChanged();
//            }
//        }
//        return stack;
//    }
}
