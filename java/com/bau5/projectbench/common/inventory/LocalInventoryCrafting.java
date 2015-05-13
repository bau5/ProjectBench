package com.bau5.projectbench.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class LocalInventoryCrafting extends InventoryCrafting {
    private IInventory parent;

    public LocalInventoryCrafting(IInventory parentInventory) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);
        parent = parentInventory;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return parent.getStackInSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        parent.setInventorySlotContents(index, stack);
    }
}