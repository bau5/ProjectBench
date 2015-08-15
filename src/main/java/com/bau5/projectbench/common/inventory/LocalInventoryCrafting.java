package com.bau5.projectbench.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;


public class LocalInventoryCrafting extends InventoryCrafting {

    private IInventory parent;

    public LocalInventoryCrafting() {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);
    }

    public LocalInventoryCrafting(IInventory parentInventory) {
        this();
        parent = parentInventory;
    }

    public void setInventoryContents(ItemStack[] stacks) {
        if (stacks.length != getSizeInventory()) {
            return;
        }
        for (int i = 0; i < stacks.length; i++) {
            this.setInventorySlotContents(i, stacks[i]);
        }
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (parent != null) {
            return parent.getStackInSlot(index);
        } else {
            return super.getStackInSlot(index);
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (parent != null) {
            parent.setInventorySlotContents(index, stack);
        } else {
            super.setInventorySlotContents(index, stack);
        }
    }
}