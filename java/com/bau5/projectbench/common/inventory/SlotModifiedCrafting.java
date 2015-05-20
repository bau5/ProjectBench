package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.client.TileEntityProjectBench;
import com.bau5.projectbench.common.PlanHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
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
                    if(FluidContainerRegistry.isContainer(piece) && theTile.getFluidInTank() != null && theTile.getFluidInTank().isFluidEqual(piece)){
                        FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(piece);
                        if(fstack.amount <= theTile.getFluidInTank().amount){
                            piece.stackSize--;
                        }
                    }
                    if(piece.stackSize > 0) {
                        for (int i = 0; i < copy.getSizeInventory(); i++) {
                            ItemStack stackInCopy = copy.getStackInSlot(i);
                            if (stackInCopy == null)
                                continue;
                            if (OreDictionary.itemMatches(piece, stackInCopy, false)
                                    || (OreDictionary.getOreIDs(piece).length == 0 && stackInCopy.getItem().equals(piece.getItem()))) {
                                copy.decrStackSize(i, 1);
                                piece.stackSize--;
                                break;
                            }
                        }
                    }
                    if(piece.stackSize > 0){
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
        this.onCrafting(stack);
        if(theTile.isUsingPlan()){
            //Get plan recipe
            ItemStack[] components = PlanHelper.getComponentsForPlan(theTile.getPlan());
            //Set plan recipe to temporary matrix
            LocalInventoryCrafting temp = new LocalInventoryCrafting();
            temp.setInventoryContents(components);
            //Find any items that will take damage instead of being "consumed"
            for(int craftingInv = 0; craftingInv < temp.getSizeInventory(); craftingInv++){
                ItemStack craftingStacks = temp.getStackInSlot(craftingInv);
                if(craftingStacks != null){
                    for(int providerInv = provider.getSupplyStart(); providerInv < provider.getSupplyStop(); providerInv++){
                        ItemStack providerStack = provider.getStackFromSupplier(providerInv);
                        if(providerStack != null && (!FluidContainerRegistry.isFilledContainer(providerStack)
                                && OreDictionary.getOreIDs(craftingStacks).length == 0 && craftingStacks.getItem().equals(providerStack.getItem()))){
                            temp.setInventorySlotContents(craftingInv, providerStack);  //Set the temp matrix with the actual item that will be used, preservers damage value
                            break;
                        }
                    }
                }
            }
            FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, temp);
            ForgeHooks.setCraftingPlayer(playerIn);
            ItemStack[] containerItems = CraftingManager.getInstance().func_180303_b(temp, playerIn.worldObj);
            ForgeHooks.setCraftingPlayer(null);
            //Consume all items in grid
            for(int craftingInv = 0; craftingInv < components.length; craftingInv++){
                ItemStack piece = temp.getStackInSlot(craftingInv);
                if(piece == null)
                    continue;
                if(FluidContainerRegistry.isContainer(piece) && theTile.getFluidInTank() != null && theTile.getFluidInTank().isFluidEqual(piece)){
                    FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(piece);
                    if(fstack.amount <= theTile.getFluidInTank().amount){
                        theTile.drain(EnumFacing.UP, fstack.amount, true);
                        temp.decrStackSize(craftingInv, 1);
                        if(containerItems[craftingInv] != null){
                            containerItems[craftingInv] = null;
                        }
                        continue;
                    }
                }
                for(int i = provider.getSupplyStart(); i < provider.getSupplyStop(); i++){
                    ItemStack stackInInventory = provider.getStackFromSupplier(i);
                    if(stackInInventory == null)
                        continue;
                    if(OreDictionary.itemMatches(piece, stackInInventory, false)
                            || (OreDictionary.getOreIDs(piece).length == 0 && piece.getItem().equals(stackInInventory.getItem()))){
                        theTile.decrStackSize(i, 1);
                        temp.decrStackSize(craftingInv, 1);
                        break;
                    }
                }
            }
            //Find any items left over in the grid (will only be items that take damage, instead of leaving).
            //Add them back to the inventory.
            for(int i = 0; i < temp.getSizeInventory(); i++){
                ItemStack leftOver = containerItems[i];
                if(leftOver == null){
                    leftOver = temp.getStackInSlot(i);
                }
                if(leftOver != null){
                    if(!theTile.addStackToInventory(leftOver)){
                        if(!playerIn.inventory.addItemStackToInventory(leftOver)){
                            playerIn.dropPlayerItemWithRandomChoice(leftOver,false);
                        }
                    }
                }
            }
        }else {
            FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, theTile.getCrafter());
            ForgeHooks.setCraftingPlayer(playerIn);
            ItemStack[] containerItems = CraftingManager.getInstance().func_180303_b(theTile.getCrafter(), playerIn.worldObj);
            ForgeHooks.setCraftingPlayer(null);
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
                                if(theTile.getHasFluidUpgrade() && theTile.getFluidInTank() != null ){
                                    FluidStack fromCrafting = FluidContainerRegistry.getFluidForFilledItem(stackInSlot);
                                    FluidStack fromTank = theTile.getFluidInTank();
                                    if(fromCrafting != null && fromTank.amount > 0 && fromTank.isFluidEqual(fromCrafting)){
                                        theTile.drain(EnumFacing.UP, fromCrafting.amount, true);
                                    }
                                    found = true;
                                    break;
                                }
                                if (!found && ItemStack.areItemsEqual(stackInSlot, supplyStack)) {
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
        theTile.forceUpdateRecipe();
        provider.supplyOreDictItems(false);
    }
}
