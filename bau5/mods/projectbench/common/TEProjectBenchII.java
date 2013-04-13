package bau5.mods.projectbench.common;

import java.util.ArrayList;
import java.util.List;

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
import bau5.mods.projectbench.common.recipes.RecipeManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TEProjectBenchII extends TileEntity implements IInventory, ISidedInventory
{
	public IInventory craftSupplyMatrix;
	private ItemStack[] inv;
	private boolean updateNeeded = false;
	private boolean shouldConsolidate = true;
	private boolean init = true;
	private int inventoryStart;
	private int supplyMatrixSize;
	
	private int sync =  0;
	
	private ItemStack[] consolidatedStacks = null;
	private ArrayList<ItemStack> listToDisplay = new ArrayList();
	
	public TEProjectBenchII(){
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
		if(sync == 20 && init && !worldObj.isRemote){
			RecipeManager.print("Sending to client...");
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
//		RecipeManager.print(resultToRemove +" " +worldObj.isRemote +" " +listToDisplay.size());
		for(int i = 0; i < listToDisplay.size(); i++){
			if(listToDisplay.get(i).getItem().equals(resultToRemove.getItem()))
				listToDisplay.remove(i);
		}
//		RecipeManager.print(" " +worldObj.isRemote +listToDisplay.size());
		updateNeeded = true;
		ItemStack stack;
//		RecipeManager.print("--List");
		for(int i = 0; i < inventoryStart; i++){
			stack = (i < listToDisplay.size()) ? listToDisplay.get(i) : null;
			inv[i] = stack;
//			if(inv[i] != null)
//				RecipeManager.print("\t" +inv[i]);
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
		RecipeManager.print("List is being set. " +((list == null) ? "null" : list.size()) +" " +((worldObj == null) ? "unknown" : worldObj.isRemote));
	}
	
	public ArrayList<ItemStack> getDisplayList(){
		return listToDisplay;
	}
	
	public void updateOutputRecipes(){
		setListForDisplay(RecipeManager.instance().getValidRecipesByStacks(consolidateItemStacks(true)));
		updateNeeded = true;
		RecipeManager.print(listToDisplay.size());
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
	
	public ItemStack[] consolidateItemStacks(boolean override){
		if(!override && !shouldConsolidate){
			return consolidatedStacks;
		}
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack stack = null;
		for(int i = 0; i < supplyMatrixSize; i++){
			stack = getStackInSlot(i + inventoryStart);
			if(stack!= null){
				items.add(stack);
			}
		}
		List<ItemStack> consolidatedItems = new ArrayList();
		main : for(ItemStack stackInArray : items){
			if(stackInArray == null)
				continue main;
			if(consolidatedItems.size() == 0)
				consolidatedItems.add(stackInArray.copy());
			else{
				int counter = 0;
				for(ItemStack stackInList : consolidatedItems){
					counter++;
					if(stackInList.getItem().equals(stackInArray.getItem())){
						stackInList.stackSize++;
						continue main;
					}else if(counter == consolidatedItems.size()){
						consolidatedItems.add(stackInArray.copy());
						continue main;
					}
				}
			}
		}
		ItemStack[] stacks = new ItemStack[consolidatedItems.size()];
		for(int i = 0; i < stacks.length; i++){
			stacks[i] = consolidatedItems.get(i);
		}
		consolidatedStacks = stacks;
		shouldConsolidate = false;
		return stacks;
	}

	public boolean consumeItems(ItemStack[] items) {
		if(!checkForItems(items.clone())){
			return false;
		}
		ItemStack[] consolidatedStacks = consolidateItemStacks(false);
		boolean flag = true;
		main : for(ItemStack is : items){
			for(ItemStack stackInInventory : consolidatedStacks){
				if(is != null){
					if(is.getItem().equals(stackInInventory.getItem())){
						boolean success = consumeItemStack(is);
						if(!success){
							flag = false;
							break;
						}
						continue main;
					}
				}
			}
		}
		flag = true;
		for(ItemStack stack : items){
			if(stack.stackSize != 0)
				flag = false;
		}
		updateNeeded = true;
		if(flag)
			worldObj.playSoundEffect((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D, "random.click", 0.1F, 1.0F);
		return flag;
	}
	public boolean consumeItemStack(ItemStack toConsume){
		ItemStack stack = toConsume.copy();
		ItemStack stackInInventory = null;
		for(int i = 0; i < supplyMatrixSize; i++){
			stackInInventory = inv[i + inventoryStart];
			if(stackInInventory == null){
				continue;
			}else{
				if(stackInInventory.getItem().equals(stack.getItem())){
					if(stack.stackSize <= stackInInventory.stackSize){
						decrStackSize(i + inventoryStart, stack.stackSize);
						stack.stackSize = 0;
						break;
					}else{
						decrStackSize(i + inventoryStart, stackInInventory.stackSize);
						stack.stackSize -= stackInInventory.stackSize;
					}
				}
			}
		}
		if(stack.stackSize == 0){
			toConsume.stackSize = 0;
		}
		return (toConsume.stackSize == 0);
	}
	
	public void sendListClientSide(){
		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 15D, worldObj.getWorldInfo().getDimension(), getDescriptionPacket());
	}
	
	private boolean checkForItems(ItemStack[] items) {
		ItemStack[] consolidatedStacks = consolidateItemStacks(false);
		ItemStack[] clonedItems = new ItemStack[items.length];
		for(int i = 0; i < clonedItems.length; i++){
			clonedItems[i] = ItemStack.copyItemStack(items[i]);
		}
		ItemStack stack = null;
		for(int i = 0; i < items.length; i++){
			stack = clonedItems[i];
//			RecipeManager.print(stack);
			for(ItemStack sin : consolidatedStacks){
				if(sin.getItem().equals(stack.getItem())){
					if(sin.stackSize >= stack.stackSize){
						stack.stackSize = 0;
//						RecipeManager.print("Found bigger stack: " +sin.stackSize);
						break;
					}else{
						stack.stackSize -= sin.stackSize;
//						RecipeManager.print("Found smaller stack " +sin.stackSize +" : " +stack.stackSize); 
					}
				}
			}
		}
		for(ItemStack toCheck : clonedItems){
			if(toCheck.stackSize != 0)
				return false;
		}
		
		return true;
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
//		if(slot >= 0 && slot < 27){
//			if(listToDisplay.size() > slot)
//				inv[slot] = listToDisplay.get(slot);
//		}
//		else{
		shouldConsolidate = true;
//			updateOutputRecipes();
		inv[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
		}
//		}
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		if(i >= 27)
			return true;
		else return false;
	}

}