package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.inventory.CraftingItemsProvider;
import com.bau5.projectbench.common.inventory.LocalInventoryCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

/**
 * Created by bau5 on 4/15/2015.
 */
public class TileEntityProjectBench extends TileEntity implements IUpdatePlayerListBox, IInventory{

    private ItemStack result;
    private boolean usingPlan = false;

    private ItemStack[] inventory = new ItemStack[28];
    private LocalInventoryCrafting crafter = new LocalInventoryCrafting(this);
    private IInventory craftResult = new InventoryCraftResult();
    private int planIndex = getSizeInventory()-1;


    private CraftingItemsProvider supplier = new CraftingItemsProvider(this, 9, 27);

    private boolean shouldUpdateRecipe = false;

    private void checkAndMarkForRecipeUpdate(int index){
        if((index >= 0 && index < 9) || index == planIndex)
            shouldUpdateRecipe = true;
    }

    public void forceUpdateRecipe() {
        shouldUpdateRecipe = true;
    }

    public boolean updateRecipe(){
        return shouldUpdateRecipe;
    }

    public boolean isUsingPlan() {
        return usingPlan;
    }

    @Override
    public void update() {
        if(updateRecipe()){
            findRecipe();
            if(getResult() == null && craftingMatrixIsEmpty()){
                setResult(getPlanResult());
            }
            shouldUpdateRecipe = false;
        }
    }

    public ItemStack getPlanResult(){
        ItemStack plan = getPlan();
        if(plan != null && plan.hasTagCompound() && plan.getTagCompound().hasKey("Result")){
            ItemStack result = ItemStack.loadItemStackFromNBT(plan.getTagCompound().getCompoundTag("Result"));
            if(result != null)
                usingPlan = true;
            return result;
        }
        return null;
    }

    public boolean craftingMatrixIsEmpty(){
        boolean flag = true;
        for(int i = 0; i < 9; i++){
            if(inventory[i] != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void findRecipe() {
        for (int i = 0; i < 9; i++) {
            crafter.setInventorySlotContents(i, inventory[i]);
        }
        ItemStack result = CraftingManager.getInstance().findMatchingRecipe(crafter, worldObj);
        if (result != null && ProjectBench.tryOreDictionary) {
            List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
            IRecipe using = null;
            for (IRecipe recipe : recipes) {
                if (recipe.getRecipeOutput() != null) {
                    if (recipe.getRecipeOutput().isItemEqual(result)) {
                        using = recipe;
                        break;
                    }
                }
            }
            if (using instanceof ShapedOreRecipe || using instanceof ShapelessOreRecipe) {
                supplier.supplyOreDictItems(true);
            } else {
                supplier.supplyOreDictItems(false);
            }
        }
        setResult(result);
        usingPlan = false;
    }

    public ItemStack getPlan() {
        return inventory[planIndex];
    }


    public LocalInventoryCrafting getCrafter() {
        return crafter;
    }

    public IInventory getCraftResult() {
        return craftResult;
    }

    public void setResult(ItemStack res) {
        craftResult.setInventorySlotContents(0, res);
        this.result = res;
    }

    public ItemStack getResult() {
        return result;
    }

    public CraftingItemsProvider getCraftingItemsProvider() {
        return supplier;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    public ItemStack getStackInSlot(int index)
    {
        return this.inventory[index];
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventory[index] != null)
        {
            ItemStack itemstack;

            if (this.inventory[index].stackSize <= count)
            {
                itemstack = this.inventory[index];
                this.inventory[index] = null;
                this.markDirty();
                checkAndMarkForRecipeUpdate(index);
                return itemstack;
            }
            else
            {
                itemstack = this.inventory[index].splitStack(count);

                if (this.inventory[index].stackSize == 0)
                {
                    this.inventory[index] = null;
                }

                this.markDirty();
                checkAndMarkForRecipeUpdate(index);
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.inventory[index] != null)
        {
            ItemStack itemstack = this.inventory[index];
            this.inventory[index] = null;
            checkAndMarkForRecipeUpdate(index);
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        checkAndMarkForRecipeUpdate(index);
        this.markDirty();
    }

    public boolean addStackToInventory(ItemStack item) {
        int firstNull = -1;
        for(int i = supplier.getSupplyStart(); i < supplier.getSupplyStop(); i++) {
            if(firstNull == -1 && getStackInSlot(i) == null) {
                firstNull = i;
                continue;
            }
            if(ItemStack.areItemsEqual(item, getStackInSlot(i))
                    && getStackInSlot(i).stackSize + item.stackSize < getStackInSlot(i).getMaxStackSize()){
                ItemStack stack = getStackInSlot(i);
                stack.stackSize += item.stackSize;
                setInventorySlotContents(i, stack);
                return true;
            }
        }
        if(firstNull != -1){
            setInventorySlotContents(firstNull, item);
            return true;
        }
        return false;
    }


    @Override
    public Packet getDescriptionPacket() {
        S35PacketUpdateTileEntity packet = null;
        if(getResult() != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("Result", result.writeToNBT(new NBTTagCompound()));
            packet = new S35PacketUpdateTileEntity(pos, 0, tag);
        }
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if(pkt.getNbtCompound() != null && pkt.getNbtCompound().hasKey("Result")){
            setResult(ItemStack.loadItemStackFromNBT(pkt.getNbtCompound().getCompoundTag("Result")));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();

        for(int i = 0; i < this.inventory.length; ++i){
            if(this.inventory[i] != null){
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        compound.setTag("Items", list);
        if(getResult() != null){
            compound.setTag("Result", getResult().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = compound.getTagList("Items", 10);

        for(int i = 0; i < list.tagCount(); ++i){
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int j = tag. getByte("Slot") & 255;
            if(j >= 0 && j < this.inventory.length){
               setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(tag));
            }
        }

        if(compound.hasKey("Result")){
            setResult(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Result")));
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return "Project Bench";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return new ChatComponentText("Project Bench");
    }
}
