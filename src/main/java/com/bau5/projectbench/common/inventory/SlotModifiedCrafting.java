package com.bau5.projectbench.common.inventory;

import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.utils.OreDictRecipeHelper;
import com.bau5.projectbench.common.utils.PlanHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;


/**
 * Created by bau5 on 4/16/2015.
 */
public class SlotModifiedCrafting extends SlotCrafting {

    private TileEntityProjectBench theTile;
    private CraftingItemsProvider provider;

    public SlotModifiedCrafting(CraftingItemsProvider provider, EntityPlayer player, TileEntityProjectBench tile, IInventory craftResult, int index, int dispX, int dispY) {
        super(player, tile.getCrafter(), craftResult, index, dispX, dispY);
        theTile = tile;
        this.provider = provider;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if (theTile.isUsingPlan()) {
            InventoryBasic copy = new InventoryBasic("local", false, 18);
            for (int i = 0; i < 18; i++) {
                ItemStack stack = theTile.getStackInSlot(i + 9);
                if (stack != null) {
                    stack = stack.copy();
                }
                copy.setInventorySlotContents(i, stack);
            }
            ItemStack[] components = PlanHelper.getComponentsForPlan(theTile.getPlan());
            LocalInventoryCrafting crafting = new LocalInventoryCrafting();
            crafting.setInventoryContents(components);
            theTile.findMatchingRecipe(crafting, playerIn.worldObj);
            if (theTile.getCurrentRecipe() != null && ! theTile.getCurrentRecipe().getRecipeOutput().getIsItemStackEqual(PlanHelper.getPlanResult(theTile.getPlan()))) {
                return false;
            }
            if (components != null) {
                boolean complete = true;
                int indexInRecipe = - 1;
                for (ItemStack piece : components) {
                    if (piece == null) {
                        continue;
                    }
                    indexInRecipe++;
                    if (FluidContainerRegistry.isContainer(piece) && theTile.getFluidInTank() != null && theTile.getFluidInTank().isFluidEqual(piece)) {
                        FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(piece);
                        if (fstack.amount <= theTile.getFluidInTank().amount) {
                            piece.stackSize--;
                        }
                    }
                    if (piece.stackSize > 0) {
                        ArrayList<ItemStack> alts = new ArrayList<ItemStack>();
                        boolean useOreDict = OreDictRecipeHelper.getIsOreDictAndFill(theTile.getCurrentRecipe(), indexInRecipe, piece, alts);
                        for (int i = 0; i < copy.getSizeInventory(); i++) {
                            ItemStack stackInCopy = copy.getStackInSlot(i);
                            if (stackInCopy == null) {
                                continue;
                            }
                            if (useOreDict) {
                                boolean breakFlag = false;
                                for (ItemStack alternativeStack : alts) {
                                    if (OreDictionary.itemMatches(alternativeStack, stackInCopy, false)) {
                                        copy.decrStackSize(i, 1);
                                        piece.stackSize--;
                                        breakFlag = true;
                                        break;
                                    }
                                }
                                if (breakFlag) {
                                    break;
                                }
                            } else if (OreDictionary.itemMatches(piece, stackInCopy, false)
                                    || (OreDictionary.getOreIDs(piece).length == 0 && stackInCopy.getItem().equals(piece.getItem()))) {
                                copy.decrStackSize(i, 1);
                                piece.stackSize--;
                                break;
                            }
                        }
                    }
                    if (piece.stackSize > 0) {
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

    private ItemStack[] fireCraftingEvents(EntityPlayer playerIn, ItemStack stack, InventoryCrafting inv) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, inv);
        ForgeHooks.setCraftingPlayer(playerIn);
        ItemStack[] containerItems = CraftingManager.getInstance().func_180303_b(inv, playerIn.worldObj);
        ForgeHooks.setCraftingPlayer(null);
        return containerItems;
    }

    private boolean isFluid(ItemStack container) {
        return FluidContainerRegistry.isContainer(container) && theTile.getFluidInTank() != null
                && theTile.getFluidInTank().isFluidEqual(container);
    }

    private boolean isDamagedInCrafting(ItemStack piece) {
        return piece.stackSize > 1 && piece.getMaxDamage() > 0;
    }

    private boolean itemEquals(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem().equals(stack2.getItem());
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        this.onCrafting(stack);
        if (theTile.isUsingPlan()) {
            //Get plan recipe
            ItemStack[] components = PlanHelper.getComponentsForPlan(theTile.getPlan());
            //Set plan recipe to temporary matrix
            LocalInventoryCrafting temp = new LocalInventoryCrafting();
            temp.setInventoryContents(components);
            ItemStack[] containerItems = fireCraftingEvents(playerIn, stack, temp);
            //Consume all item in grid
            int indexInCrafting = - 1;
            for (int craftingInv = 0; craftingInv < components.length; craftingInv++) {
                ItemStack piece = temp.getStackInSlot(craftingInv);
                if (piece == null) {
                    continue;
                }
                indexInCrafting++;
                //Try first to use the tank, if it's a fluid component
                if (isFluid(piece)) {
                    FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(piece);
                    if (fstack.amount <= theTile.getFluidInTank().amount) {
                        theTile.drain(EnumFacing.UP, fstack.amount, true);
                        temp.decrStackSize(craftingInv, 1);
                        if (containerItems[craftingInv] != null) {
                            containerItems[craftingInv] = null;
                        }
                        continue;
                    }
                }
                ArrayList<ItemStack> alts = new ArrayList<ItemStack>();
                boolean useOreDict = OreDictRecipeHelper.getIsOreDictAndFill(theTile.getCurrentRecipe(), indexInCrafting, piece, alts);
                for (int i = provider.getSupplyStart(); i < provider.getSupplyStop(); i++) {
                    ItemStack stackInInventory = provider.getStackFromSupplier(i);
                    if (stackInInventory == null) {
                        continue;
                    }
                    if (itemEquals(piece, stackInInventory) && isDamagedInCrafting(piece)) {      //Item that takes damage and is duplicated in crafting grid.
                        if (piece.getItemDamage() > 1) {                                          //Old plans didn't handle this correctly, set damage to one for correct damaging.
                            piece.setItemDamage(1);
                        }
                        stackInInventory.setItemDamage(stackInInventory.getItemDamage() + piece.getItemDamage());
                        temp.decrStackSize(craftingInv, piece.stackSize);                       //The second item is copied back to the tile inventory later.
                        break;
                    }
                    if (useOreDict) {
                        boolean breakFlag = false;
                        for (ItemStack alternativeStack : alts) {
                            if (OreDictionary.itemMatches(alternativeStack, stackInInventory, false)) {
                                theTile.decrStackSize(i, 1);
                                temp.decrStackSize(craftingInv, 1);
                                if (FluidContainerRegistry.isContainer(piece)) {
                                    temp.setInventorySlotContents(craftingInv, FluidContainerRegistry.drainFluidContainer(piece.copy()));
                                }
                                breakFlag = true;
                                break;
                            }
                        }
                        if (breakFlag) {
                            break;
                        }
                    } else if (OreDictionary.itemMatches(piece, stackInInventory, false)) {
                        theTile.decrStackSize(i, 1);
                        temp.decrStackSize(craftingInv, 1);
                        break;
                    }
                }
            }
            //Find any item left over in the grid (will only be item that take damage, instead of leaving).
            //Add them back to the inventory.
            for (int i = 0; i < temp.getSizeInventory(); i++) {
                ItemStack leftOver = containerItems[i];
                if (leftOver == null) {
                    leftOver = temp.getStackInSlot(i);
                }
                if (leftOver != null) {
                    if (! theTile.addStackToInventory(leftOver)) {
                        if (! playerIn.inventory.addItemStackToInventory(leftOver)) {
                            playerIn.dropPlayerItemWithRandomChoice(leftOver, false);
                        }
                    }
                }
            }
        } else {
            ItemStack[] containerItems = fireCraftingEvents(playerIn, stack, theTile.getCrafter());
            int indexInRecipe = - 1;
            for (int i = 0; i < containerItems.length; ++ i) {
                ItemStack stackInSlot = theTile.getStackInSlot(i);
                ItemStack containerItem = containerItems[i];
                boolean found = false;
                if (stackInSlot != null) {
                    ItemStack match = stackInSlot;
                    if (stackInSlot.stackSize == 1) {
                        //Begin check for OreRecipe, set that up
                        indexInRecipe++;
                        ArrayList<ItemStack> alts = new ArrayList<ItemStack>();
                        boolean useOreDict = OreDictRecipeHelper.getIsOreDictAndFill(theTile.getCurrentRecipe(), indexInRecipe, match, alts);
                        //End Ore dict check
                        //Begin Search for match
                        for (int supplyInv = provider.getSupplyStart(); supplyInv < provider.getSupplyStop(); supplyInv++) {
                            ItemStack supplyStack = provider.getStackFromSupplier(supplyInv);
                            if (supplyStack == null) {
                                continue;
                            }
                            if (containerItem != null) {
                                if (theTile.getHasFluidUpgrade() && theTile.getFluidInTank() != null) {
                                    FluidStack fromCrafting = FluidContainerRegistry.getFluidForFilledItem(stackInSlot);
                                    FluidStack fromTank = theTile.getFluidInTank();
                                    if (fromCrafting != null && fromTank.amount > 0 && fromTank.isFluidEqual(fromCrafting)) {
                                        theTile.drain(EnumFacing.UP, fromCrafting.amount, true);
                                        found = true;
                                    }
                                    break;
                                }
                                if (! found && ItemStack.areItemsEqual(stackInSlot, supplyStack)) {
                                    theTile.decrStackSize(supplyInv, 1);
                                    if (! theTile.addStackToInventory(containerItem)) {
                                        playerIn.dropPlayerItemWithRandomChoice(containerItem, false);
                                    }
                                    found = true;
                                    break;
                                }
                            }
                            if (supplyStack != null && useOreDict) {  //Use ore dict if this item in the recipe does.
                                for (ItemStack alternativeStack : alts) {
                                    if (OreDictionary.itemMatches(alternativeStack, supplyStack, ! useOreDict)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (! found) {
                                    found = OreDictionary.itemMatches(match, supplyStack, ! useOreDict);
                                }
                                if (found) {
                                    theTile.decrStackSize(supplyInv, 1);
                                    theTile.forceUpdateRecipe();
                                }
                            } else if (supplyStack != null && OreDictionary.itemMatches(match, supplyStack, useOreDict)) {
                                theTile.decrStackSize(supplyInv, 1);
                                found = true;
                                theTile.forceUpdateRecipe();
                            }
                            if (found) {
                                break;
                            }
                        }
                    }
                    if (! found) {
                        if (stackInSlot != null) {
                            theTile.decrStackSize(i, 1);
                        }

                        if (containerItem != null) {
                            if (theTile.getStackInSlot(i) == null) {
                                theTile.setInventorySlotContents(i, containerItem);
                            } else if (! theTile.addStackToInventory(containerItem)) {
                                if (! playerIn.inventory.addItemStackToInventory(containerItem)) {
                                    playerIn.dropPlayerItemWithRandomChoice(containerItem, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        theTile.forceUpdateRecipe();
    }
}
