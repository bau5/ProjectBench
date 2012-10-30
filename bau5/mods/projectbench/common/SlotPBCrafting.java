package bau5.mods.projectbench.common;

import bau5.mods.projectbench.common.TileEntityProjectBench.LocalInventoryCrafting;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotCrafting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotPBCrafting extends SlotCrafting
{
	private EntityPlayer thePlayer;
	private final IInventory craftMatrix;
	private IInventory craftResultMatrix;
	//This is actually the TileEntity, need to use the 
	//tileEntity's inventory to satisfy crafting.
	private IInventory craftSupplyMatrix;

	public SlotPBCrafting(Container parent, EntityPlayer player, IInventory craftingMatrix, 
						  IInventory craftingResultMatrix, IInventory craftingSupplyMatrix, 
									                int slotID, int xDisplay, int yDisplay) 
	{
		super(player, craftingMatrix, craftingResultMatrix, slotID, xDisplay, yDisplay);
		thePlayer = player;
		craftMatrix = craftingMatrix;
		craftResultMatrix = craftingResultMatrix;
		craftSupplyMatrix = craftingSupplyMatrix;
	}
	
	// TODO Fix behavior for this inventory!!!
	@Override
	public void onPickupFromSlot(ItemStack stack)
    {
		boolean found = false;
        GameRegistry.onItemCrafted(thePlayer, stack, craftMatrix);
        this.onCrafting(stack);
        
        //Looping through crafting matrix finding required items
        for(int invIndex = 0; invIndex < 9; invIndex++)
        {
        	found = false;
        	//Grabs the item for comparison
        	ItemStack craftComponentStack = craftSupplyMatrix.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		//Checking the supply inventory for matching item
        		for(int supplyInv = 9; supplyInv < 27; supplyInv++)
        		{
        			//Grabs the item in the supply Matrix
        			ItemStack supplyMatrixStack = craftSupplyMatrix.getStackInSlot(supplyInv);
        			if(supplyMatrixStack != null)
        			{
        				if(supplyMatrixStack.getItem().equals(craftComponentStack.getItem()))
        				{
        					//Found item!
        					System.out.println("Found matching item in craftSupply! " + found);
        					found = true;
        					craftSupplyMatrix.decrStackSize(supplyInv, 1);
        					break;
        				}
        			}
        		}
        		//Didn't find it in the supply inventory, remove from crafting matrix
        		if(!found)
        		{
        			System.out.println("Found matching item in craftMatrix!");
        			craftSupplyMatrix.decrStackSize(invIndex, 1);
        		}
        	}
        }
    }
}