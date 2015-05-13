package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.client.TileEntityProjectBench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

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
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, theTile.getCrafter());
        this.onCrafting(stack);
        ForgeHooks.setCraftingPlayer(playerIn);
        ItemStack[] containerItems = CraftingManager.getInstance().func_180303_b(theTile.getCrafter(), playerIn.worldObj);
        ForgeHooks.setCraftingPlayer(null);
        for (int i = 0; i < containerItems.length; ++i) {
            ItemStack stackInSlot = theTile.getStackInSlot(i);
            ItemStack containerItem = containerItems[i];
            boolean found = false;
            if (stackInSlot != null) {
                if(stackInSlot.stackSize == 1) {
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
                        if(theTile.getStackInSlot(i) == null) {
                            theTile.setInventorySlotContents(i, containerItem);
                        }else if(!theTile.addStackToInventory(containerItem)){
                            if(!playerIn.inventory.addItemStackToInventory(containerItem)){
                                playerIn.dropPlayerItemWithRandomChoice(containerItem, false);
                            }
                        }
                    }
                }
            }
        }
        provider.supplyOreDictItems(false);
    }
}
