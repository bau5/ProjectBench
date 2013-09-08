package bau5.mods.projectbench.common.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.ProjectBench;

public class SlotPBPlan extends Slot{

	public SlotPBPlan(IInventory inv, int index, int xPos, int yPos) {
		super(inv, index, xPos, yPos);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack.getItem().equals(ProjectBench.instance.pbPlan);
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
