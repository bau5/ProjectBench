package bau5.mods.projectbench.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotCrafting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotPBCrafting extends SlotCrafting
{
	private Container parentContainer;
	private EntityPlayer thePlayer;
	private IInventory craftMatrix;
	private IInventory craftResultMatrix;
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
		parentContainer = parent;
	}

	@Override
	public void onPickupFromSlot(ItemStack stack)
    {
        GameRegistry.onItemCrafted(thePlayer, stack, craftMatrix);
        this.onCrafting(stack);
        
        for(int invIndex = 0; invIndex < craftSupplyMatrix.getSizeInventory(); ++invIndex)
        {
        	ItemStack craftComponentStack = craftSupplyMatrix.getStackInSlot(invIndex);
        	if(craftComponentStack != null)
        	{
        		craftSupplyMatrix.decrStackSize(invIndex, 1);
        		if (craftComponentStack.getItem().hasContainerItem())
                {
                    ItemStack containerItem = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);

                    if (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !this.thePlayer.inventory.addItemStackToInventory(containerItem))
                    {
                        if (this.craftSupplyMatrix.getStackInSlot(invIndex) == null)
                        {
                            this.craftSupplyMatrix.setInventorySlotContents(invIndex, containerItem);
                        }
                        else
                        {
                            this.thePlayer.dropPlayerItem(containerItem);
                        }
                    }
                }
        		parentContainer.onCraftMatrixChanged(craftMatrix);
        		return;
        	}
        }
        for (int var2 = 0; var2 < this.craftMatrix.getSizeInventory(); ++var2)
        {
            ItemStack var3 = this.craftMatrix.getStackInSlot(var2);

            if (var3 != null)
            {
                this.craftMatrix.decrStackSize(var2, 1);

                if (var3.getItem().hasContainerItem())
                {
                    ItemStack var4 = var3.getItem().getContainerItemStack(var3);

                    if (!var3.getItem().doesContainerItemLeaveCraftingGrid(var3) || !this.thePlayer.inventory.addItemStackToInventory(var4))
                    {
                        if (this.craftMatrix.getStackInSlot(var2) == null)
                        {
                            this.craftMatrix.setInventorySlotContents(var2, var4);
                        }
                        else
                        {
                            this.thePlayer.dropPlayerItem(var4);
                        }
                    }
                }
            }
        }
//        for (int invIndex = 0; invIndex < craftMatrix.getSizeInventory(); ++invIndex)
//        {
//            ItemStack craftingMatrixStackEquiv = craftMatrix.getStackInSlot(invIndex);
//            Item craftingItemToFind = null;
//            if(craftingMatrixStackEquiv != null)
//            {
//                craftingItemToFind = craftingMatrixStackEquiv.getItem();
//            } else continue;
//            ItemStack supplyMatrixStackEquiv = null;
//            
//            for(int supplyInvIndex = 0; supplyInvIndex < craftSupplyMatrix.getSizeInventory(); supplyInvIndex++)
//            {
//            	if(craftSupplyMatrix.getStackInSlot(supplyInvIndex) != null && craftSupplyMatrix.getStackInSlot(supplyInvIndex).getItem().equals(craftingItemToFind))
//            	{
//            		supplyMatrixStackEquiv = craftSupplyMatrix.getStackInSlot(supplyInvIndex);
//            	}
//            }
//
//            if (supplyMatrixStackEquiv != null)
//            {
//                this.craftSupplyMatrix.decrStackSize(invIndex, 1);
//
//                if (supplyMatrixStackEquiv.getItem().hasContainerItem())
//                {
//                    ItemStack stack3 = supplyMatrixStackEquiv.getItem().getContainerItemStack(supplyMatrixStackEquiv);
//                    
//                    if (stack3.isItemStackDamageable() && stack3.getItemDamage() >= stack3.getMaxDamage())
//                    {
//                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, stack3));
//                        stack3 = null;
//                    }
//
//                    if (stack3 != null && (!supplyMatrixStackEquiv.getItem().doesContainerItemLeaveCraftingGrid(supplyMatrixStackEquiv) || !this.thePlayer.inventory.addItemStackToInventory(stack3)))
//                    {
//                        if (this.craftSupplyMatrix.getStackInSlot(invIndex) == null)
//                        {
//                            this.craftSupplyMatrix.setInventorySlotContents(invIndex, stack3);
//                        }
//                        else
//                        {
//                            this.thePlayer.dropPlayerItem(stack3);
//                        }
//                    }
//                }
//            }
//        }
        }
}
