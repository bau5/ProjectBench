package bau5.mods.projectbench.common;

import java.util.ArrayList;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityCraftingFrame extends EntityItemFrame implements IEntityAdditionalSpawnData{

	private int stackSize = -1;
	private RecipeItem currentRecipe = null;
	
	public EntityCraftingFrame(World world, int x, int y, int z,
			int dir) {
		super(world, x, y, z, dir);
	}
	
	public EntityCraftingFrame(World world){
		super(world);
	}
	@Override
	public boolean interact(EntityPlayer player) {
		if(player == null)
			return true;
		if(getDisplayedItem() == null){
            ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && !this.worldObj.isRemote)
            {
        		currentRecipe = RecipeManager.instance().searchForRecipe(itemStack);
        		if(currentRecipe == null)
        			return true;
        		
            	stackSize = currentRecipe.result().stackSize;
                this.setDisplayedItem(currentRecipe.result().copy());
                if (!player.capabilities.isCreativeMode && --itemStack.stackSize <= 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                }
            }
	        else if (!this.worldObj.isRemote)
	        {
	            this.setItemRotation(this.getRotation() + 1);
	        }
	        return true;	
		}
		if(player.isSneaking()){
			dispenseItem(getDisplayedItem());
			reset();
			return false;
		}
		
		ArrayList<ItemStack[]> stacks = RecipeManager.instance().getComponentsToConsume(getDisplayedItem());
		if(stacks != null){
			for(ItemStack[] isa : stacks){
				if(consumeItems(isa, player)){
					ItemStack stack = getDisplayedItem().copy();
					stack.stackSize = stackSize;
					dispenseItem(stack);
					return true;
				}
			}
		}
		return false;
	}
	private void dispenseItem(ItemStack stack){
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
        double d0 = bsi.getX() + 0.7D * (double)enumFacing.getFrontOffsetX();
        double d1 = bsi.getY() + 0.5D * (double)enumFacing.getFrontOffsetY();
        double d2 = bsi.getZ() + 0.7D * (double)enumFacing.getFrontOffsetZ();
        PositionImpl iPos = new PositionImpl(d0, d1, d2);
		BehaviorDefaultDispenseItem.doDispense(worldObj, stack, 1, enumFacing, iPos);
	}
	
	public boolean consumeItems(ItemStack[] items, EntityPlayer player) {
		ItemStack[] check = new ItemStack[items.length];
		for(int i = 0; i < items.length; i++)
			check[i] = ItemStack.copyItemStack(items[i]);
		ItemStack stackOnPlayer = null;
		boolean haveAll = true;
		boolean wild = false;
		main:for(ItemStack stackToConsume : check){
			for(int i = 0; i < player.inventory.mainInventory.length; i++){
				stackOnPlayer = player.inventory.mainInventory[i];
				
				if(stackOnPlayer != null && OreDictionary.itemMatches(stackToConsume, stackOnPlayer, false)){
					if(stackOnPlayer.stackSize >= stackToConsume.stackSize){
						stackToConsume.stackSize = 0;
						continue main;
					}else{
						stackToConsume.stackSize -= stackOnPlayer.stackSize;
					}
				}
			}
			if(stackToConsume.stackSize != 0){
				haveAll = false;
				break;
			}
		}
		if(haveAll){
			main:for(ItemStack stackToConsume : items){
				for (int i = 0; i < player.inventory.mainInventory.length; ++i)
		        {
					stackOnPlayer = player.inventory.mainInventory[i];
		            if (stackOnPlayer != null && OreDictionary.itemMatches(stackToConsume, stackOnPlayer, false))
		            {
		            	if(stackOnPlayer.stackSize > stackToConsume.stackSize){
		            		stackOnPlayer.stackSize -= stackToConsume.stackSize;
		            		stackToConsume.stackSize = 0;
		            		continue main;
		            	}
		            	else{
		            		stackToConsume.stackSize -= stackOnPlayer.stackSize;
		            		stackOnPlayer.stackSize = 0;
		            		player.inventory.mainInventory[i] = null;
		            	}
		            }
		        }
			}
		}
		return haveAll;
	}
	
	@Override
	public void dropItemStack()
    {
        this.entityDropItem(new ItemStack(ProjectBench.instance.craftingFrame), 0.0F);
        ItemStack itemstack = this.getDisplayedItem();

        if (itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.setItemFrame((EntityItemFrame)null);
            this.entityDropItem(itemstack, 0.0F);
        }
    }
	public void reset(){
		dataWatcher = new DataWatcher();
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        entityInit();
        interact(null);
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound mainTag) {
		if(this.getDisplayedItem() != null){
			mainTag.setInteger("stackSize", stackSize);
		}
		super.writeEntityToNBT(mainTag);
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound mainTag) {
		NBTTagCompound itemTag = mainTag.getCompoundTag("Item");
		if(itemTag != null && !itemTag.hasNoTags()){
			if(mainTag.hasKey("stackSize"));
				stackSize = mainTag.getInteger("stackSize");
		}
		super.readEntityFromNBT(mainTag);
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(hangingDirection);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		hangingDirection = data.readInt();
	}
}
