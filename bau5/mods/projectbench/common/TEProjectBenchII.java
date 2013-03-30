package bau5.mods.projectbench.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TEProjectBenchII extends TileEntity implements IInventory, ISidedInventory
{
	public IInventory craftSupplyMatrix;
	private ItemStack[] inv;
	private boolean updateNeeded = true;
	
	private ArrayList<ItemStack> listToDisplay = new ArrayList<ItemStack>();
	
	public TEProjectBenchII(){
		craftSupplyMatrix = new InventoryBasic("pbIICraftingSupply", true, 18);
		inv = new ItemStack[45];
	}
	
	public void forceUpdate(){
		updateNeeded = true;
		updateEntity();
	}
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(updateNeeded){
			disperseListAcrossMatrix();
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
		System.out.println("Disperse for " +str +worldObj.isRemote);
		for(int i = 0; i < 27; i++){
			stack = (i < listToDisplay.size()) ? listToDisplay.get(i) : null;
			inv[i] = stack;
			if(inv[i] != null)
				System.out.println("\t" +inv[i]);
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
		System.out.println(resultToRemove +" " +worldObj.isRemote +" " +listToDisplay.size());
		for(int i = 0; i < listToDisplay.size(); i++){
			if(listToDisplay.get(i).getItem().equals(resultToRemove.getItem()))
				listToDisplay.remove(i);
		}
		System.out.println(" " +worldObj.isRemote +listToDisplay.size());
		updateNeeded = true;
		ItemStack stack;
		System.out.println("--List");
		for(int i = 0; i < 27; i++){
			stack = (i < listToDisplay.size()) ? listToDisplay.get(i) : null;
			inv[i] = stack;
			if(inv[i] != null)
				System.out.println("\t" +inv[i]);
		}
	}
	
	public void clearMatrix(){
		for(int i = 0; i < 27; i++){
			inv[i] = null;
		}
	}
	
	public void setListForDisplay(ArrayList<ItemStack> list){
		listToDisplay = list;
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
			System.out.println("" +stack +" appears in " +str +" inventory.");
		else
			System.out.println("" +stack +" doesn't appear in " +str +" inventory.");
		if(flag1)
			System.out.println("" +stack +" is in " +str +" list.");
		else
			System.out.println("" +stack +" isn't in " +str +" list.");
	}
	
	public ItemStack[] consolidateItemStacks(){
		ArrayList<ItemStack> items = new ArrayList();
		ItemStack stack = null;
		for(int i = 0; i < 18; i++){
			stack = getStackInSlot(i + 27);
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
		return stacks;
	}

	public boolean consumeItems(ItemStack[] items) {
		if(!checkForItems(items.clone())){
			return false;
		}
		ItemStack[] consolidatedStacks = consolidateItemStacks();
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
	
	private boolean checkForItems(ItemStack[] items) {
		ItemStack[] consolidatedStacks = consolidateItemStacks();
		ItemStack[] clonedItems = new ItemStack[items.length];
		for(int i = 0; i < clonedItems.length; i++){
			clonedItems[i] = ItemStack.copyItemStack(items[i]);
		}
		ItemStack stack = null;
		for(int i = 0; i < items.length; i++){
			stack = clonedItems[i];
			System.out.println(stack);
			for(ItemStack sin : consolidatedStacks){
				if(sin.getItem().equals(stack.getItem())){
					if(sin.stackSize >= stack.stackSize){
						stack.stackSize = 0;
						System.out.println("Found bigger stack: " +sin.stackSize);
						break;
					}else{
						stack.stackSize -= sin.stackSize;
						System.out.println("Found smaller stack " +sin.stackSize +" : " +stack.stackSize); 
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

	public boolean consumeItemStack(ItemStack toConsume){
		ItemStack stack = toConsume.copy();
		ItemStack stackInInventory = null;
		for(int i = 0; i < 18; i++){
			stackInInventory = inv[i + 27];
			if(stackInInventory == null){
				continue;
			}else{
				if(stackInInventory.getItem().equals(stack.getItem())){
					if(stack.stackSize <= stackInInventory.stackSize){
						decrStackSize(i + 27, stack.stackSize);
						stack.stackSize = 0;
						break;
					}else{
						decrStackSize(i + 27, stackInInventory.stackSize);
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
	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		ItemStack stack;
		if(i >= 0 && i < 27)
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
		if(slot >= 0 && slot < 27){
			if(listToDisplay.size() > slot)
				inv[slot] = listToDisplay.get(slot);
		}
		else{
			inv[slot] = stack;
			if(stack != null && stack.stackSize > getInventoryStackLimit())
			{
				stack.stackSize = getInventoryStackLimit();
			}
		}
	}

	@Override
	public String getInvName() {
		return "Project Bench Mk. II";
	}

	@Override
	public boolean func_94042_c() {
		return false;
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
	public boolean func_94041_b(int i, ItemStack itemstack) {
		return false;
	}
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return 0;
	}

}