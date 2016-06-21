package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.TileEntityProjectBench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ContainerProjectBench extends Container {
    private TileEntityProjectBench tile;

    public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb){
        this.tile = tpb;
        bindPlayerInventory(invPlayer);
        int i;
        int j;
        int index = 0;
        addSlotToContainer(new SlotModifiedCrafting(tile.getCraftingItemsProvider(),
                            invPlayer.player, tile, tile.getCraftResult(), 0, 124, 35));
        for(i = 0; i < 3; i++){
            for(j = 0; j < 3; j++){
                addSlotToContainer(new Slot(tile, index++, 30 + j * 18, 17 + i * 18));
            }
        }
        for(i = 0; i < 2; i++){
            for(j = 0; j < 9; j++){
                if(i == 1){
                    addSlotToContainer(new Slot(tile, 18 + j, 8 + j * 18, (i * 2 -1) + 77 + i * 18));
                }else{
                    addSlotToContainer(new Slot(tile, 9 + j, 8 + j * 18, 77 + i * 18));
                }
            }
        }
        addSlotToContainer(new SlotPlan(tile, 27, 7, 35));
    }

    @Override
    public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId == 36 && clickTypeIn == ClickType.PICKUP_ALL){
            clickTypeIn = ClickType.PICKUP;
        }

        return super.func_184996_a(slotId, dragType, clickTypeIn, player);
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer) {
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 121 + i * 18));
            }
        }
        for (i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 179));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()){
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            boolean success = false;
            if(stack.getItem().equals(ProjectBench.plan) && index != 64){
                if(!inventorySlots.get(64).getHasStack()){
                    if(!this.mergeItemStack(stack, 64, 65, false))
                        return null;
                    success = true;
                }else{
                    ItemStack leftOver = transferStackInSlot(playerIn, 64);
                    if(leftOver != null){
                        if(!this.mergeItemStack(stack, 64, 65, false)){
                            return null;
                        }
                        success = true;
                    }else{
                        if(index < 46 || index > 64){
                            if(!this.mergeItemStack(stack,46, 64, true))
                                return null;
                            success = true;
                        }

                    }
                }
            }
            if(!success) {
                if(index == 64){
                    if(!this.mergeItemStack(stack, 46, 64, true))
                        return null;
                }else if (index >= 46 && index <= 63) {
                    if (!this.mergeItemStack(stack, 0, 36, false))
                        return null;
                } else if (index >= 36 && index <= 45) {
                    if (!this.mergeItemStack(stack, 46, 64, false)) {
                        if (!this.mergeItemStack(stack, 0, 36, false)) {
                            return null;
                        }
                    }
                } else if (index >= 0 && index <= 35) {
                    if (!this.mergeItemStack(stack, 46, 64, false))
                        return null;
                }
            }

            if(stack.stackSize == 0) {
                slot.putStack(null);
            }else{
                slot.onSlotChanged();
            }
            if (stack.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, stack);
        }

        return itemstack;
    }

    public TileEntityProjectBench getTileEntity() {
        return tile;
    }
}
