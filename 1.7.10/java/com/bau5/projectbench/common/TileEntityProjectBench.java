package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.CraftingItemsProvider;
import com.bau5.projectbench.common.inventory.LocalInventoryCrafting;
import com.bau5.projectbench.common.upgrades.FluidUpgrade;
import com.bau5.projectbench.common.upgrades.IUpgrade;
import com.bau5.projectbench.common.utils.PlanHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Iterator;

/**
 * Created by bau5 on 4/15/2015.
 */
public class TileEntityProjectBench extends TileEntity implements IInventory, IFluidHandler{
    private int fluidUpdateTick = 0;

    private FluidTank fluidTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);

    private ItemStack result;
    private IRecipe currentRecipe;
    private boolean usingPlan = false;

    private ItemStack[] inventory = new ItemStack[28];
    private LocalInventoryCrafting crafter = new LocalInventoryCrafting(this);
    private IInventory craftResult = new InventoryCraftResult();
    private int planIndex = getSizeInventory()-1;

    private IUpgrade upgrade = null;

    private CraftingItemsProvider supplier = new CraftingItemsProvider(this, 9, 27);

    private boolean shouldUpdateRecipe = false;
    private boolean shouldSendNetworkUpdate = false;

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
    public void updateEntity() {
        if(updateRecipe()){
            findRecipe();
            if(getResult() == null && craftingMatrixIsEmpty()){
                setResult(getPlanResult());
            }
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            shouldUpdateRecipe = false;
        }
        if(++fluidUpdateTick >= 20 && sendNetworkUpdate()){
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            shouldSendNetworkUpdate = false;
            fluidUpdateTick = 0;
        }
    }

    private boolean sendNetworkUpdate() {
        return shouldSendNetworkUpdate;
    }


    public void performUpgrade(ItemStack upgradeItem) {
        switch(upgradeItem.getItemDamage()){
            case 1: upgrade = new FluidUpgrade();
                break;
        }
        shouldSendNetworkUpdate = true;
    }

    public IUpgrade getUpgrade(){
        return upgrade;
    }

    public boolean getCanAcceptUpgrade(){
        return getUpgrade() == null;
    }

    public ItemStack getPlanResult(){
        ItemStack plan = getPlan();
        if(plan != null){
            ItemStack result = PlanHelper.getPlanResult(plan);
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
        ItemStack result = findMatchingRecipe(crafter, worldObj);
        if(result == null)
            currentRecipe = null;
        setResult(result);
        usingPlan = false;
    }

    public ItemStack findMatchingRecipe(InventoryCrafting inventoryCrafting, World worldIn)
    {
        Iterator iterator = CraftingManager.getInstance().getRecipeList().iterator();
        IRecipe irecipe;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            irecipe = (IRecipe)iterator.next();
        }
        while (!irecipe.matches(inventoryCrafting, worldIn));

        currentRecipe = irecipe;

        return irecipe.getCraftingResult(inventoryCrafting);
    }

    public IRecipe getCurrentRecipe(){
        return currentRecipe;
    }

    public boolean isOreRecipe(){
        return currentRecipe instanceof ShapedOreRecipe || currentRecipe instanceof ShapelessOreRecipe;
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

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean addStackToInventory(ItemStack item) {
        int firstNull = -1;
        for(int i = supplier.getSupplyStart(); i < supplier.getSupplyStop(); i++) {
            if(firstNull == -1 && getStackInSlot(i) == null) {
                firstNull = i;
                continue;
            }
            if(OreDictionary.itemMatches(item, getStackInSlot(i), true)
                    && getStackInSlot(i).stackSize + item.stackSize <= getStackInSlot(i).getMaxStackSize()){
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
        NBTTagCompound tag = new NBTTagCompound();
        if(getResult() != null) {
            tag.setTag("Result", result.writeToNBT(new NBTTagCompound()));
        }
        if(upgrade != null) {
            tag.setInteger("Upgrade", upgrade.getType());
            if(upgrade.getType() == 0){
                if(getFluidInTank() != null)
                    tag.setTag("Fluid", fluidTank.getFluid().writeToNBT(new NBTTagCompound()));
            }
        }
        if(!tag.hasNoTags())
            packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.func_148857_g();
        if(tag == null)
            return;
        if(tag.hasKey("Result")){
            setResult(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Result")));
        }
        if(tag.hasKey("Upgrade")){
            int type = tag.getInteger("Upgrade");
            switch(type){
                case 0: upgrade = new FluidUpgrade();
                    fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid")));
                    break;
            }
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
        if(upgrade != null){
            compound.setInteger("Upgrade", upgrade.getType());
            if(upgrade.getType() == 0 && fluidTank.getFluid() != null){
                NBTTagCompound fluidTag = getFluidInTank().writeToNBT(new NBTTagCompound());
                compound.setTag("Fluid", fluidTag);
            }
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
        if(compound.hasKey("Upgrade")){
            int type = compound.getInteger("Upgrade");
            switch(type){
                case 0: upgrade = new FluidUpgrade();
                    if(compound.hasKey("Fluid")) {
                        FluidStack fstack = FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("Fluid"));
                        fluidTank.setFluid(fstack);
                    }
                    break;
            }
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
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

    public boolean getHasFluidUpgrade(){
        return upgrade != null && upgrade.getType() == 0;
    }

    @Override
    public int fill(ForgeDirection dir, FluidStack resource, boolean doFill) {
        if(!getHasFluidUpgrade() || !canFill(dir, resource.getFluid()))
            return 0;
        shouldSendNetworkUpdate = true;
        return fluidTank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection dir, FluidStack resource, boolean doDrain) {
        if(!getHasFluidUpgrade() && fluidTank.getFluidAmount() > 0)
            return null;
        if(resource.isFluidEqual(getFluidInTank())){
            drain(dir, resource.amount, doDrain);
        }
        shouldSendNetworkUpdate = true;
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if(!getHasFluidUpgrade())
            return null;
        shouldSendNetworkUpdate = true;
        return fluidTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return getHasFluidUpgrade() && fluidTank.getFluidAmount() < fluidTank.getCapacity();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return getHasFluidUpgrade();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if(getFluidInTank() != null){
            return new FluidTankInfo[]{
                new FluidTankInfo(getFluidInTank(), FluidContainerRegistry.BUCKET_VOLUME * 16)
            };
        }
        return new FluidTankInfo[0];
    }

    public FluidStack getFluidInTank(){
        return fluidTank.getFluid();
    }
}
