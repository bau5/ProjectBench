package com.bau5.projectbench.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by bau5 on 4/16/2015.
 */
public class CraftingItemsProvider {
    private int supplyStart;
    private int supplyStop;
    private IInventory provider;

    public CraftingItemsProvider(IInventory inv, int first, int last){
        supplyStart = first;
        supplyStop  = last;
        provider = inv;
    }

    public int getSupplyStop() {
        return supplyStop;
    }

    public int getSupplyStart(){
        return supplyStart;
    }

    public int getSize() {
        return supplyStop - supplyStart;
    }

    public ItemStack getStackFromSupplier(int supplyInv) {
        return provider.getStackInSlot(supplyInv);
    }
}
