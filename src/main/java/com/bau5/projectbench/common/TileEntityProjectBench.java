package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.CraftingItemsProvider;
import com.bau5.projectbench.common.inventory.LocalInventoryCrafting;
import com.bau5.projectbench.common.upgrades.FluidUpgrade;
import com.bau5.projectbench.common.upgrades.IUpgrade;
import com.bau5.projectbench.common.upgrades.InventorySizeUpgrade;
import com.bau5.projectbench.common.utils.PlanHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;

/**
 * Created by bau5 on 4/15/2015.
 */
public class TileEntityProjectBench extends TileEntity implements ITickable, IInventory, IFluidHandler {
    private int fluidUpdateTick = 0;

    private FluidTank fluidTank = new FluidTank(Fluid.BUCKET_VOLUME * 16);

    private ItemStack result;
    private IRecipe currentRecipe;
    private boolean usingPlan = false;

    private int inventorySize = 28;
    private ItemStack[] inventory = new ItemStack[inventorySize];

    private LocalInventoryCrafting crafter = new LocalInventoryCrafting(this);
    private IInventory craftResult = new InventoryCraftResult();
    private int planIndex = getSizeInventory() - 1;

    private IUpgrade upgrade = null;

    private CraftingItemsProvider provider = new CraftingItemsProvider(this, 9, 27);

    private boolean shouldUpdateRecipe = false;
    private boolean shouldSendNetworkUpdate = false;

    private void checkAndMarkForRecipeUpdate(int index){
        if((index >= 0 && index < 9) || index == planIndex) {
            shouldUpdateRecipe = true;
        }
    }

    public void forceUpdateRecipe() {
        shouldUpdateRecipe = true;
    }

    public boolean isUsingPlan() {
        return usingPlan;
    }

    @Override
    public void update() {
        if(shouldUpdateRecipe){
            findRecipe();
            if(getResult() == null && isCraftingMatrixEmpty()){
                setResult(getPlanResult());
            }
            worldObj.scheduleUpdate(this.getPos(), this.getBlockType(), 0);
            shouldUpdateRecipe = false;
            markDirty();
            worldObj.notifyBlockOfStateChange(pos, ProjectBench.projectBench);
        }
        if(++fluidUpdateTick >= 20 && sendNetworkUpdate()){
            worldObj.scheduleUpdate(this.getPos(), this.getBlockType(), 0);
            shouldSendNetworkUpdate = false;
            fluidUpdateTick = 0;
            markDirty();
            worldObj.notifyBlockOfStateChange(pos, ProjectBench.projectBench);
        }
    }

    private boolean sendNetworkUpdate() {
        return shouldSendNetworkUpdate;
    }

    public void performUpgrade(ItemStack upgradeItem) {
        switch(upgradeItem.getMetadata()){
            case 1:
                upgrade = new FluidUpgrade();
                break;
            case 2:
                upgrade = new InventorySizeUpgrade();

                inventorySize += ((InventorySizeUpgrade) upgrade).getAdditionalSlotCount();
                ItemStack[] newItems = new ItemStack[inventorySize];
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] != null) {
                        newItems[i] = inventory[i].copy();
                    }
                }
                inventory = newItems;

                provider = new CraftingItemsProvider(this, 9, 45);

                setInventorySlotContents(0, getStackInSlot(0));

                break;
            default:
                break;
        }
        shouldSendNetworkUpdate = true;
    }

    public boolean getCanAcceptUpgrade(){
        return upgrade == null;
    }

    private ItemStack getPlanResult(){
        ItemStack plan = getPlan();
        if(plan != null){
            ItemStack result = PlanHelper.getPlanResult(plan);
            if(result != null)
                usingPlan = true;
            return result;
        }
        return null;
    }

    private boolean isCraftingMatrixEmpty(){
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
        return provider;
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

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
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
        for(int i = provider.getSupplyStart(); i < provider.getSupplyStop(); i++) {
            if(firstNull == -1 && getStackInSlot(i) == null) {
                firstNull = i;
                continue;
            }
            ItemStack stack = getStackInSlot(i);
            if(stack != null && ItemStack.areItemsEqual(item, getStackInSlot(i))
                    && stack.stackSize + item.stackSize <= stack.getMaxStackSize()){
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        SPacketUpdateTileEntity packet = null;
        NBTTagCompound tag = new NBTTagCompound();
        if(getResult() != null) {
            tag.setTag("Result", result.writeToNBT(new NBTTagCompound()));
        }
        if(upgrade != null) {
            tag.setInteger("Upgrade", upgrade.getType());
            if(upgrade.getType() == 0){
                if(getFluidInTank() != null && fluidTank.getFluid() != null)
                    tag.setTag("Fluid", fluidTank.getFluid().writeToNBT(new NBTTagCompound()));
            }
        }
        if(!tag.hasNoTags())
            packet = new SPacketUpdateTileEntity(pos, 0, tag);
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound tag = pkt.getNbtCompound();

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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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

        return compound;
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
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

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
    public ITextComponent getDisplayName() {
        return new TextComponentString("Project Bench");
    }

    public boolean getHasFluidUpgrade(){
        return upgrade != null && upgrade.getType() == 0;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (getHasFluidUpgrade() && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) fluidTank;
        return super.getCapability(capability, facing);
    }

    public FluidStack getFluidInTank(){
        return fluidTank.getFluid();
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!getHasFluidUpgrade())
            return 0;
        if (doFill)
            shouldSendNetworkUpdate = true;
        return fluidTank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!getHasFluidUpgrade())
            return null;
        return fluidTank.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!getHasFluidUpgrade())
            return null;
        return fluidTank.drain(maxDrain, doDrain);
    }
}
