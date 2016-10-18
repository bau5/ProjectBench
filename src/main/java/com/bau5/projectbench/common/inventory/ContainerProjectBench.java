package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.TileEntityProjectBench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ContainerProjectBench extends Container {
    private TileEntityProjectBench tile;
    private int planSlot = -1;

    public ContainerProjectBench(InventoryPlayer invPlayer, TileEntityProjectBench tpb){
        this.tile = tpb;
        bindPlayerInventory(invPlayer);
        int i;
        int j;
        int index = 0;
        addSlotToContainer(new SlotModifiedCrafting(tile.getCraftingItemsProvider(),
                            invPlayer.player, tile, tile.getCraftResult(), 0, 124, 35));

        for (i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(tile, index++, 30 + (i % 3) * 18, 17 + (i / 3) * 18));
        }

        for(i = 0; i < tile.getCraftingItemsProvider().getSize() / 9; i++){
            for(j = 0; j < 9; j++){
                addSlotToContainer(new Slot(tile, index++, 8 + j * 18, 77 + i * 19));
            }
        }
        planSlot = index;
        addSlotToContainer(new SlotPlan(tile, planSlot, 7, 35));
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId == planSlot && clickTypeIn == ClickType.PICKUP_ALL){
            clickTypeIn = ClickType.PICKUP;
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer) {
        int i, j;
        int yOffset = 0;
        if (tile.getCraftingItemsProvider().getSize() > 27) {
            yOffset = 38;
        }
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 121 + i * 18 + yOffset));
            }
        }
        for (i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 179 + yOffset));
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
