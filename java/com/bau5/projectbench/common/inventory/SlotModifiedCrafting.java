package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.client.TileEntityProjectBench;
import com.bau5.projectbench.common.PlanHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by bau5 on 4/16/2015.
 */
public class SlotModifiedCrafting extends SlotCrafting {

    private TileEntityProjectBench theTile;
    private CraftingItemsProvider provider;

    public SlotModifiedCrafting(CraftingItemsProvider provider, EntityPlayer player, TileEntityProjectBench tile, IInventory craftResult, int index, int dispX, int dispY) {
        super(player, tile.getCrafter(), craftResult, index, dispX,  dispY);
        theTile = tile;
        this.provider = provider;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if(theTile.isUsingPlan()){
            InventoryBasic copy = new InventoryBasic("local", false, 18);
            for(int i = 0; i < 18; i++){
                ItemStack stack = theTile.getStackInSlot(i + 9);
                if(stack != null)
                    stack = stack.copy();
                copy.setInventorySlotContents(i, stack);
            }
            ItemStack[] components = PlanHelper.getComponentsForPlan(theTile.getPlan());
            if(components != null) {
                boolean complete = true;
                for(ItemStack piece : components){
                    if(piece == null)
                        continue;
                    for(int i = 0; i < copy.getSizeInventory(); i++){
                        ItemStack stackInCopy = copy.getStackInSlot(i);
                        if(stackInCopy != null && OreDictionary.itemMatches(piece, stackInCopy, false)) {
                            copy.decrStackSize(i, 1);
                            piece.stackSize--;
                            break;
                        }
                    }
                    if(piece.stackSize != 0){
                        complete = false;
                        break;
                    }
                }
                return complete;
            }
            return false;
        }
        return super.canTakeStack(playerIn);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, theTile.getCrafter());
        this.onCrafting(stack);
        ForgeHooks.setCraftingPlayer(playerIn);
        ItemStack[] containerItems = CraftingManager.getInstance().func_180303_b(theTile.getCrafter(), playerIn.worldObj);
        ForgeHooks.setCraftingPlayer(null);
        if(theTile.isUsingPlan()){
            ItemStack[] components = PlanHelper.getComponentsForPlan(theTile.getPlan());
            for(ItemStack piece : components){
                if(piece == null)
                    continue;
                for(int i = provider.getSupplyStart(); i < provider.getSupplyStop(); i++){
                    ItemStack stackInInventory = provider.getStackFromSupplier(i);
                    if(stackInInventory == null)
                        continue;
                    if(OreDictionary.itemMatches(piece, stackInInventory, false)){
                        theTile.decrStackSize(i, 1);
                        break;
                    }
                }
            }
            theTile.forceUpdateRecipe();
        }else {
            for (int i = 0; i < containerItems.length; ++i) {
                ItemStack stackInSlot = theTile.getStackInSlot(i);
                ItemStack containerItem = containerItems[i];
                boolean found = false;
                if (stackInSlot != null) {
                    if (stackInSlot.stackSize == 1) {
                        for (int supplyInv = provider.getSupplyStart(); supplyInv < provider.getSupplyStop(); supplyInv++) {
                            ItemStack supplyStack = provider.getStackFromSupplier(supplyInv);
                            ItemStack match = stackInSlot;
                            if (containerItem != null) {
                                if (ItemStack.areItemsEqual(stackInSlot, supplyStack)) {
                                    theTile.decrStackSize(supplyInv, 1);
                                    if (!theTile.addStackToInventory(containerItem)) {
                                        playerIn.dropPlayerItemWithRandomChoice(containerItem, false);
                                    }
                                    found = true;
                                    break;
                                }
                            }
                            if (provider.useOreDictionary()) {
                                match = stackInSlot.copy();
                                int[] ids = OreDictionary.getOreIDs(match);
                                if (ids.length > 0)
                                    match.setItemDamage(OreDictionary.WILDCARD_VALUE);
                            }
                            if (supplyStack != null && OreDictionary.itemMatches(match, supplyStack, false)) {
                                theTile.decrStackSize(supplyInv, 1);
                                found = true;
                                theTile.forceUpdateRecipe();
                            }
                            if (found)
                                break;
                        }
                    }
                    if (!found) {
                        if (stackInSlot != null) {
                            theTile.decrStackSize(i, 1);
                        }

                        if (containerItem != null) {
                            if (theTile.getStackInSlot(i) == null) {
                                theTile.setInventorySlotContents(i, containerItem);
                            } else if (!theTile.addStackToInventory(containerItem)) {
                                if (!playerIn.inventory.addItemStackToInventory(containerItem)) {
                                    playerIn.dropPlayerItemWithRandomChoice(containerItem, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        provider.supplyOreDictItems(false);
    }
}
