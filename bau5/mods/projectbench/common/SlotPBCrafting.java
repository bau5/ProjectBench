package bau5.mods.projectbench.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
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

	@Override
	public void func_82870_a(EntityPlayer player, ItemStack stack)
    {
		boolean found = false;
		boolean metaSens = false;
        GameRegistry.onItemCrafted(player, stack, craftMatrix);
        this.onCrafting(stack);

        for(int craftInvIndex = 0; craftInvIndex < 9; craftInvIndex++)
        {
        	ItemStack craftInvStack = craftSupplyMatrix.getStackInSlot(craftInvIndex);
        	if(craftInvStack != null && craftInvStack.stackSize > 1)
        	{
        		craftSupplyMatrix.decrStackSize(craftInvIndex, 1);
        	}
        }
        
        //Looping through crafting matrix finding required items
        for(int invIndex = 0; invIndex < 9; invIndex++)
        {
        	found = false;
        	//Grabs the item for comparison
        	ItemStack craftComponentStack = craftSupplyMatrix.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		if(!craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
        				&& craftComponentStack.itemID != Block.planks.blockID
        				&& craftComponentStack.itemID != Block.cloth.blockID
        				&& craftComponentStack.itemID != Block.leaves.blockID)
				{
					System.out.println("Metadata Item!");
					metaSens = true;
				}
        		//Checking the supply inventory for matching item
        		if(!(craftComponentStack.stackSize > 1))
        		{
        			for(int supplyInv = 9; supplyInv < 27; supplyInv++)
					{
		    			//Grabs the item in the supply Matrix
		    			ItemStack supplyMatrixStack = craftSupplyMatrix.getStackInSlot(supplyInv);
		    			if(supplyMatrixStack != null)
		    			{
		    				if(supplyMatrixStack.getItem().equals(craftComponentStack.getItem()))
		    				{
		    					if(metaSens)
		    					{
		    						if(craftComponentStack.getItemDamage() != supplyMatrixStack.getItemDamage())
		    						{
		    							continue;
		    						} else 
		    						{
		    							craftSupplyMatrix.decrStackSize(supplyInv, 1);
		            					found = true;
		    						}
		    					}
		    					else
		    					{
		        					craftSupplyMatrix.decrStackSize(supplyInv, 1);
		        					found = true;
		    					}
		    					//Found item!
		    					if (supplyMatrixStack.getItem().hasContainerItem())
				                {
				                    ItemStack contStack = supplyMatrixStack.getItem().getContainerItemStack(supplyMatrixStack);
				                    
				                    if (contStack.isItemStackDamageable() && contStack.getItemDamage() > contStack.getMaxDamage())
				                    {
				                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, contStack));
				                        contStack = null;
				                    }
		
				                    if (contStack != null && (!supplyMatrixStack.getItem().doesContainerItemLeaveCraftingGrid(supplyMatrixStack) || !this.thePlayer.inventory.addItemStackToInventory(contStack)))
				                    {
				                        if (this.craftMatrix.getStackInSlot(supplyInv) == null)
				                        {
				                            this.craftMatrix.setInventorySlotContents(supplyInv, contStack);
				                        }
				                        else
				                        {
				                            this.thePlayer.dropPlayerItem(contStack);
				                        }
				                    }
				                }
		    					break;
		    				}
		    			}
		    		}
        		}
        		
        		//Didn't find it in the supply inventory, remove from crafting matrix
        		if(!found)
        		{
        			craftSupplyMatrix.decrStackSize(invIndex, 1);
        			if (craftComponentStack.getItem().hasContainerItem())
                    {
                        ItemStack conStack = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);
                        
                        if (conStack.isItemStackDamageable() && conStack.getItemDamage() > conStack.getMaxDamage())
                        {
                            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, conStack));
                            conStack = null;
                        }

                        if (conStack != null && (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !this.thePlayer.inventory.addItemStackToInventory(conStack)))
                        {
                            if (this.craftMatrix.getStackInSlot(invIndex) == null)
                            {
                                this.craftMatrix.setInventorySlotContents(invIndex, conStack);
                            }
                            else
                            {
                                this.thePlayer.dropPlayerItem(conStack);
                            }
                        }
                    }
        		}
        	}
        }
    }
}
