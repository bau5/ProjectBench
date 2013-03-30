package bau5.mods.projectbench.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class SlotPBII extends Slot {
	
	public SlotPBII(IInventory inv, int index, int xDisplay, int yDisplay) {
		super(inv, index, xDisplay, yDisplay);
	}

	@Override
	public boolean isItemValid(ItemStack stack){
		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
		
    }
}
