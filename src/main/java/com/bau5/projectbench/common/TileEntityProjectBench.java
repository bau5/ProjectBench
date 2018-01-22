package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.CraftingItemsProvider;
import com.bau5.projectbench.common.inventory.LocalInventoryCrafting;
import com.bau5.projectbench.common.upgrades.FluidUpgrade;
import com.bau5.projectbench.common.upgrades.IUpgrade;
import com.bau5.projectbench.common.upgrades.InventorySizeUpgrade;
import com.bau5.projectbench.common.utils.PlanHelper;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.asm.transformers.ItemStackTransformer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;
import java.util.Arrays;


/**
 * Created by bau5 on 4/15/2015.
 */
public class TileEntityProjectBench extends TileEntity implements ITickable, IInventory, IFluidHandler {
    private int fluidUpdateTick = 0;

    // TODO: find bucket volume proper
    private FluidTank fluidTank = new FluidTank(16000);

    private ItemStack result;
    private IRecipe currentRecipe;
    private boolean usingPlan = false;

    // Default inventory size
    private int inventorySize = 28;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);

    private LocalInventoryCrafting crafter = new LocalInventoryCrafting(this);
    private IInventory craftResult = new InventoryCraftResult();

    private IUpgrade upgrade = null;

    private CraftingItemsProvider provider = new CraftingItemsProvider(this, 9, 27);

    private boolean shouldUpdateRecipe = false;
    private boolean shouldSendNetworkUpdate = false;

    private void checkAndMarkForRecipeUpdate(int index){
        if((index >= 0 && index < 9) || index == getPlanIndex()) {
            forceUpdateRecipe();
        }
    }

    public int getPlanIndex() {
        return inventorySize - 1;
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
            if(getResult() == ItemStack.EMPTY && craftingMatrixIsEmpty()){
                setResult(getPlanResult());
            }
            world.scheduleUpdate(this.getPos(), this.getBlockType(), 0);
            shouldUpdateRecipe = false;
        }
        if(++fluidUpdateTick >= 20 && sendNetworkUpdate()){
            world.scheduleUpdate(this.getPos(), this.getBlockType(), 0);
            shouldSendNetworkUpdate = false;
            fluidUpdateTick = 0;
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
                NonNullList<ItemStack> newInventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
                for (int i = 0; i < inventory.size(); i++) {
                    newInventory.set(i, inventory.get(i));
                }
                inventory = newInventory;

                provider = new CraftingItemsProvider(this, 9, 45);

                setInventorySlotContents(0, getStackInSlot(0));

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
        ItemStack result = PlanHelper.getPlanResult(getPlan());
        if(result != ItemStack.EMPTY)
            usingPlan = true;
        return result;
    }

    public boolean craftingMatrixIsEmpty(){
        boolean flag = true;
        for(int i = 0; i < 9; i++){
            if(!inventory.get(i).isEmpty()) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void findRecipe() {
        for (int i = 0; i < 9; i++) {
            crafter.setInventorySlotContents(i, inventory.get(i));
        }
        ItemStack result = findMatchingRecipe(crafter, world);
        if(result == ItemStack.EMPTY)
            currentRecipe = null;
        setResult(result);
        usingPlan = false;
    }

    public ItemStack findMatchingRecipe(InventoryCrafting inventoryCrafting, World worldIn)
    {
        currentRecipe = CraftingManager.findMatchingRecipe(inventoryCrafting, worldIn);

        if (currentRecipe == null) {
            return ItemStack.EMPTY;
        }
        return currentRecipe.getCraftingResult(inventoryCrafting);
    }

    public IRecipe getCurrentRecipe(){
        return currentRecipe;
    }

    public ItemStack getPlan() {
        return inventory.get(getPlanIndex());
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
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        // TODO: stub
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (!inventory.get(index).isEmpty())
        {
            ItemStack itemstack;

            if (this.inventory.get(index).getCount() <= count)
            {
                itemstack = this.inventory.get(index);
                this.inventory.set(index, ItemStack.EMPTY);
                this.markDirty();
                checkAndMarkForRecipeUpdate(index);
                return itemstack;
            }
            else
            {
                itemstack = this.inventory.get(index).splitStack(count);

                this.markDirty();
                checkAndMarkForRecipeUpdate(index);
                return itemstack;
            }
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }


    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventory.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        checkAndMarkForRecipeUpdate(index);
        this.markDirty();
    }

    public boolean addStackToInventory(ItemStack item) {
        // TODO: use this thing or something ItemHandlerHelper.insertItem()
        int firstNull = -1;
        for(int i = provider.getSupplyStart(); i < provider.getSupplyStop(); i++) {
            if(firstNull == -1 && getStackInSlot(i) == null) {
                firstNull = i;
                continue;
            }
            if(ItemStack.areItemsEqual(item, getStackInSlot(i))
                    && getStackInSlot(i).getCount() + item.getCount() <= getStackInSlot(i).getMaxStackSize()){
                ItemStack stack = getStackInSlot(i);
                stack.setCount(stack.getCount() + item.getCount());
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

    @Nullable
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
                if(getFluidInTank() != null)
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
        if(tag == null)
            return;
        if(tag.hasKey(PlanHelper.result)){
            setResult(new ItemStack(tag.getCompoundTag(PlanHelper.result)));
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
        NBTTagCompound res = super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();

        for(int i = 0; i < this.inventory.size(); ++i){
            if(!inventory.get(i).isEmpty()){
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                inventory.get(i).writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        res.setTag("Items", list);
        if(getResult() != null){
            res.setTag(PlanHelper.result, getResult().writeToNBT(new NBTTagCompound()));
        }
        if(upgrade != null){
            res.setInteger("Upgrade", upgrade.getType());
            if(upgrade.getType() == 0 && fluidTank.getFluid() != null){
                NBTTagCompound fluidTag = getFluidInTank().writeToNBT(new NBTTagCompound());
                res.setTag("Fluid", fluidTag);
            }
        }
        return res;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = compound.getTagList("Items", 10);

        for(int i = 0; i < list.tagCount(); ++i){
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int j = tag.getByte("Slot") & 255;
            if(j < this.inventory.size()){
               setInventorySlotContents(j, new ItemStack(tag));
            }
        }

        if(compound.hasKey(PlanHelper.result)){
            setResult(new ItemStack(compound.getCompoundTag(PlanHelper.result)));
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
    public boolean isUsableByPlayer(EntityPlayer player) {
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

    // TODO: What do these fields do
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

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(!getHasFluidUpgrade() || getHasFluidUpgrade())
            return 0;
        shouldSendNetworkUpdate = true;
        return fluidTank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(!getHasFluidUpgrade() && fluidTank.getFluidAmount() > 0)
            return null;
        if(resource.isFluidEqual(getFluidInTank())){
            drain(resource.amount, doDrain);
        }
        shouldSendNetworkUpdate = true;
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if(!getHasFluidUpgrade())
            return null;
        shouldSendNetworkUpdate = true;
        return fluidTank.drain(maxDrain, doDrain);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{
            new FluidTankProperties(fluidTank.getFluid(), fluidTank.getCapacity(), fluidTank.canFill(), fluidTank.canDrain())
        };
    }

    public FluidStack getFluidInTank(){
        return fluidTank.getFluid();
    }
}
