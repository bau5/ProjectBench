package bau5.mods.projectbench.common;

import java.util.ArrayList;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import bau5.mods.projectbench.common.recipes.RecipeCrafter;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * EntityCraftingFrame
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class EntityCraftingFrame extends EntityItemFrame implements IEntityAdditionalSpawnData{

	private int stackSize = -1;
	private boolean hit = false;
	private RecipeItem currentRecipe = null;
	private RecipeCrafter theCrafter = new RecipeCrafter();
	public int id = -1;
	
	public EntityCraftingFrame(World world, int x, int y, int z,
			int dir) {
		super(world, x, y, z, dir);
	}
	
	public EntityCraftingFrame(World world){
		super(world);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float f) {
		ItemStack stack = getDisplayedItem();
		if(stack != null && source.getSourceOfDamage() != null){
			if(source.getSourceOfDamage().getClass() == EntityPlayerMP.class ||
					source.getSourceOfDamage().getClass() == EntityClientPlayerMP.class){
				stack.stackSize = 1;
				if(!worldObj.isRemote)
					dispenseItem(stack);
				reset();
				return true;
			}
		}
		return super.attackEntityFrom(source, f);
	}
	
	@Override
	public boolean func_130002_c(EntityPlayer player) {
		if(player == null || !ProjectBench.MKII_ENABLED)
			return true;
		if(getDisplayedItem() == null){
            ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && !this.worldObj.isRemote)
            {
        		currentRecipe = RecipeManager.instance().searchForRecipe(itemStack);
        		if(currentRecipe == null)
        			return true;
                this.setDisplayedItem(currentRecipe.result().copy());
                stackSize = 1;
        		
                if (!player.capabilities.isCreativeMode && --itemStack.stackSize <= 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                }
            }
	        return true;	
		}else if(this.getDisplayedItem() != null && currentRecipe == null || RecipeCrafter.checkItemMatch(currentRecipe.result(), getDisplayedItem(), false)){
			currentRecipe = RecipeManager.instance().searchForRecipe(getDisplayedItem());
		}
		
		ArrayList<ItemStack[]> stacks = RecipeManager.instance().getComponentsToConsume(currentRecipe.result());
		if(stacks != null){
			ItemStack[] consolidatedInventory = null;
			for(ItemStack[] isa : stacks){
				theCrafter.addInventoryReference(player.inventory.mainInventory);
				consolidatedInventory = theCrafter.consolidateItemStacks(player.inventory.mainInventory);
				int numMade = theCrafter.consumeItems(isa, consolidatedInventory, currentRecipe.result(), player.isSneaking());
				if(numMade != 0){
					ItemStack toDispense = currentRecipe.result();
					toDispense.stackSize *= numMade;
					if(!worldObj.isRemote)
						dispenseItem(toDispense);	
					theCrafter.onItemCrafted(toDispense, player.worldObj, player, numMade);
					break;
				}
			}
		}
		return false;
	}
	protected void dispenseItem(ItemStack stack){
		// 2  0  3  1
		//10 11 12 13
		// N  S  E  W
		int dir = -1;
		switch(hangingDirection){
		case 0: dir = 11; break;
		case 1: dir = 12; break;
		case 2: dir = 10; break;
		case 3: dir = 13; break;
		}
		dir = dir & 7;
		BlockSourceImpl bsi = new BlockSourceImpl(worldObj, xPosition, yPosition, zPosition);
		EnumFacing enumFacing = EnumFacing.getFront(dir);
        double d0 = bsi.getX() + 0.7D * enumFacing.getFrontOffsetX();
        double d1 = bsi.getY() + 0.5D * enumFacing.getFrontOffsetY();
        double d2 = bsi.getZ() + 0.7D * enumFacing.getFrontOffsetZ();
        PositionImpl iPos = new PositionImpl(d0, d1, d2);
        if(stack.stackSize > stack.getMaxStackSize()){
        	int maxSize = stack.getMaxStackSize();
        	int leftOver = 0;
        	int count = 0;
        	while(stack.stackSize > stack.getMaxStackSize()){
        		count++;
        		stack.stackSize -= stack.getMaxStackSize();
        	}
        	leftOver = stack.stackSize;
        	ItemStack temp = ItemStack.copyItemStack(stack);
        	temp.stackSize = temp.getMaxStackSize();
        	for(int i = 0; i < count; i++){
        		BehaviorDefaultDispenseItem.doDispense(worldObj, temp, 1, enumFacing, iPos);
        	}
        	if(leftOver > 0){
        		temp.stackSize = leftOver;
        		BehaviorDefaultDispenseItem.doDispense(worldObj, temp, 1, enumFacing, iPos);
        	}
        }else
    		BehaviorDefaultDispenseItem.doDispense(worldObj, stack, 1, enumFacing, iPos);
	}
	@Override
	public void onUpdate() {
		super.onUpdate();
		if(currentRecipe == null && getDisplayedItem() != null)
			currentRecipe = RecipeManager.instance().searchForRecipe(getDisplayedItem());
	}
	
	@Override
	public void setDisplayedItem(ItemStack par1ItemStack)
    {
        par1ItemStack = par1ItemStack.copy();
        par1ItemStack.setItemFrame(this);
        this.getDataWatcher().updateObject(2, par1ItemStack);
        this.getDataWatcher().setObjectWatched(2);
    }
	
	@Override
	public void func_110128_b(Entity par1Entity) {
        this.entityDropItem(new ItemStack(ProjectBench.instance.craftingFrame), 0.5F);
	}
	public void reset(){
		currentRecipe = null;
		dataWatcher = new DataWatcher();
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        entityInit();
        func_130002_c(null);
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound mainTag) {
		if(getDisplayedItem() != null){
			mainTag.setInteger("stackSize", stackSize);
		}
		super.writeEntityToNBT(mainTag);
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound mainTag) {
		NBTTagCompound itemTag = mainTag.getCompoundTag("Item");
		if(itemTag != null && !itemTag.hasNoTags()){
			if(mainTag.hasKey("stackSize")){
				stackSize = mainTag.getInteger("stackSize");
			}
		}
		super.readEntityFromNBT(mainTag);
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(xPosition);
		data.writeInt(yPosition);
		data.writeInt(zPosition);
		data.writeInt(hangingDirection);
		data.writeInt(stackSize);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		xPosition = data.readInt();
		yPosition = data.readInt();
		zPosition = data.readInt();
		hangingDirection = data.readInt();
		stackSize = data.readInt();
	}

	public RecipeItem getCurrentRecipe() {
		return currentRecipe;
	}
}
