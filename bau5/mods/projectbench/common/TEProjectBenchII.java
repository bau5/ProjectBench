package bau5.mods.projectbench.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import bau5.mods.projectbench.common.recipes.RecipeCrafter;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * 
 * TEProjectBenchII
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class TEProjectBenchII extends TileEntity implements IInventory, ISidedInventory
{
	public IInventory craftSupplyMatrix;
	private ItemStack[] inv;
	private boolean updateNeeded = false;
	private boolean shouldConsolidate = true;
	public boolean initSlots = false;
	public int inventoryStart;
	public int supplyMatrixSize;
	private byte directionFacing = 0;
	
	private int sync =  0;
	
	private ItemStack[] consolidatedStacks = null;
	private ArrayList<ItemStack> listToDisplay = new ArrayList();
	private RecipeCrafter theCrafter = new RecipeCrafter();
	
	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
	}
	public TEProjectBenchII(){
		theCrafter.addTPBReference(this);
		craftSupplyMatrix = new InventoryBasic("pbIICraftingSupply", true, 18);
		inv = new ItemStack[45];
		inventoryStart = 27;
		supplyMatrixSize = 18;
	}
	
	public void forceUpdate(){
		updateNeeded = true;
		updateEntity();
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		sync++;
		if(updateNeeded){
			disperseListAcrossMatrix();
		}
		if(sync == 20 && initSlots && !worldObj.isRemote){
			sendListClientSide();
		}
		
	}

	public void disperseListAcrossMatrix(){
		updateNeeded = false;
		ItemStack stack = null;
		if(listToDisplay.size() == 0)
			return;
		String str;
		if(worldObj.isRemote){
			str = "server";
		}else{
			str = "client";
		}
//		RecipeManager.print("Disperse for " +str +worldObj.isRemote);
		for(int i = 0; i < inventoryStart; i++){
			stack = (i < listToDisplay.size()) ? listToDisplay.get(i) : null;
			inv[i] = stack;
//			if(inv[i] != null)
//				RecipeManager.print("\t" +inv[i]);
		}
	}
	
	public void scrambleMatrix(){
		ArrayList<ItemStack> temp = new ArrayList();
		if(listToDisplay.isEmpty())
			return;
		int targetSize = listToDisplay.size();
		int counter = 0;
		ItemStack stack;
		for(int i = 0; i < targetSize; i++){
			if(i == 0)
				stack = listToDisplay.get(targetSize / 2);
			else{
				if(i % 2 == 0){
					stack = listToDisplay.get(targetSize / 2 + counter); 
				}else{
					stack = listToDisplay.get(targetSize / 2 - counter++);
				}
				temp.add(stack);
			}
		}
		temp.add(listToDisplay.get(0));
		setListForDisplay(temp);
	}
	
	public void removeResultFromDisplay(ItemStack resultToRemove){
		if(resultToRemove == null)	
				return;
		for(int i = 0; i < listToDisplay.size(); i++){
			if(listToDisplay.get(i).getItem().equals(resultToRemove.getItem()))
				listToDisplay.remove(i);
		}
		updateNeeded = true;
		ItemStack stack;
		for(int i = 0; i < inventoryStart; i++){
			stack = (i < listToDisplay.size()) ? listToDisplay.get(i) : null;
			inv[i] = stack;
		}
	}
	
	public void clearMatrix(){
		for(int i = 0; i < inventoryStart; i++){
			inv[i] = null;
		}
	}
	
	public void setListForDisplay(ArrayList<ItemStack> list){
		listToDisplay = list;
		updateNeeded = true;
		if(worldObj != null)
			System.out.printf("List is being set for %s with %d entries.\n", worldObj.getClass().getSimpleName(), list.size());
	}
	
	public ArrayList<ItemStack> getDisplayList(){
		return listToDisplay;
	}
	
	public void updateOutputRecipes(){
		setListForDisplay(RecipeManager.instance().getValidRecipesByStacks(consolidateItemStacks(true)));
		updateNeeded = true;
	}
	
	public void checkListAndInventory(ItemStack stack){
		String str = (worldObj.isRemote) ? "server" : "client";
		boolean flag = false, flag1 = false;
		if(stack == null)
			return;
		for(ItemStack is : inv){
			if(is == null)
				continue;
			if(is.getItem().equals(stack.getItem()))
				flag = true;
		}
		for(ItemStack si : listToDisplay){
			if(si.getItem().equals(stack.getItem()))
				flag1 = true;
		}
		if(flag)
			RecipeManager.print("" +stack +" appears in " +str +" inventory.");
		else
			RecipeManager.print("" +stack +" doesn't appear in " +str +" inventory.");
		if(flag1)
			RecipeManager.print("" +stack +" is in " +str +" list.");
		else
			RecipeManager.print("" +stack +" isn't in " +str +" list.");
	}

	public int consumeItems(ItemStack[] items, ItemStack resultStack, boolean max) {
		theCrafter.addInventoryReference(createInventoryReference());
		return theCrafter.consumeItems(items, consolidateItemStacks(false), resultStack, max);
	}
	
	public ItemStack[] consolidateItemStacks(boolean override){
		if(!override && !shouldConsolidate){
			return consolidatedStacks;
		}else{
			ItemStack[] stackArr = new ItemStack[supplyMatrixSize];
			for(int i = 0; i < supplyMatrixSize; i++){
				stackArr[i] = ItemStack.copyItemStack(getStackInSlot(i + inventoryStart));
			}
			return theCrafter.consolidateItemStacks(stackArr);
		}
	}
	
	public ItemStack[] createInventoryReference(){
		ItemStack[]	newArr = new ItemStack[supplyMatrixSize];
		for(int i = 0; i < supplyMatrixSize; i++){
			newArr[i] = inv[i + inventoryStart];
		}
		return newArr;
	}
	public void sendListClientSide(){
		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 15D, worldObj.getWorldInfo().getDimension(), getDescriptionPacket());
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return PBPacketHandler.prepPacketMkII(this);
	}
	public int[] getInputStacksForPacket()
	{
		int[] craftingStacks = new int[(supplyMatrixSize * 3)];
		int index = 0;
		for(int i = 0; i < supplyMatrixSize; i++)
		{
			if(inv[i +inventoryStart] != null)
			{
				craftingStacks[index++] = inv[i +inventoryStart].itemID;
				craftingStacks[index++] = inv[i +inventoryStart].stackSize;
				craftingStacks[index++] = inv[i +inventoryStart].getItemDamage();
			} else
			{
				craftingStacks[index++] = 0;
				craftingStacks[index++] = 0;
				craftingStacks[index++] = 0;
			}
		}
		return craftingStacks;
	}
	public void buildResultFromPacket(int[] stacksData)
	{
		if(stacksData == null)
		{
			return;
		}
		if(stacksData.length != 0)
		{
			int index = 0;
			for(int i = 0; i < supplyMatrixSize; i++)
			{
				if(stacksData[index + 1] != 0)
				{
					ItemStack stack = new ItemStack(stacksData[index], stacksData[index+1], stacksData[index+2]);
					inv[i +inventoryStart] = stack;
				}
				else
				{
					inv[i +inventoryStart] = null;
				}
				index = index + 3;
			}
		}
		updateOutputRecipes();
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		ItemStack stack;
		if(i >= 0 && i < inventoryStart)
			if(listToDisplay.size() > i)
				stack = listToDisplay.get(i).copy();
			else return null;
		else
			stack = inv[i];
		return stack;
	}

	public ItemStack[] getInventory(){
		return inv.clone();
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = getStackInSlot(slot);
		if(slot < 27)
			return stack;
		if(stack != null)
		{
			if(stack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			} else
			{
				stack = stack.splitStack(amount);
				if(stack.stackSize == 0) 
				{
					setInventorySlotContents(slot, null);
				}
			}
		}
		shouldConsolidate = true;
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {		
		shouldConsolidate = true;
		inv[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
		}
		if(slot >= 27 && slot < 45 && !initSlots)
			updateOutputRecipes();
	}
	
	@Override
	public String getInvName() {
		return "Project Bench Mk. II";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
	{
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord +0.5, yCoord +0.5, zCoord +0.5) < 64;
	}

	@Override
	public void openChest() { }

	@Override
	public void closeChest() { }


	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		setDirection(tagCompound.getByte("facing"));
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if(slot >= 0 && slot < inv.length)
			{
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		
		tagCompound.setByte("facing", directionFacing);
		NBTTagList itemList = new NBTTagList();	
		
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];
			if(stack != null)
			{
				NBTTagCompound tag = new NBTTagCompound();	
				tag.setByte("Slot", (byte)i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	@Override
	public int getStartInventorySide(ForgeDirection side) {
		return 27;
	}
	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 18;
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		if(i >= 27)
			return true;
		else return false;
	}

	public void setDirection(byte dir) {
		directionFacing = dir;
		System.out.println("Direction: " +dir +((worldObj != null) ? worldObj.isRemote : "null"));
	}
	public byte getDirection(){
		return directionFacing;
	}

}