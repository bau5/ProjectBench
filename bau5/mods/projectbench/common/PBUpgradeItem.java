package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import bau5.mods.projectbench.common.tileentity.TEProjectBenchII;
import bau5.mods.projectbench.common.tileentity.TileEntityProjectBench;

/**
 * PBUpgradeItem
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class PBUpgradeItem extends Item	
{
	private int type;
	private final String[] itemNames = { "Mk. I", "Mk. II" };
	public PBUpgradeItem(int id, int meta) 
	{
		super(id);
		type = meta;
		setMaxDamage(0);
		setMaxStackSize(16);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		super.addInformation(stack, player, list, bool);
		switch(type){
		case 0: list.add("\u00A77" + "Used to upgrade a Crafting Bench."); 
			break;
		case 1: list.add("\u00A77" + "Used to upgrade a Project Bench."); 
			break;
		}
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack) {
		return "Project Bench Upgrade " + itemNames[type];
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		switch(type){
		case 0: itemIcon = register.registerIcon("projectbench:pbup");
			break;
		case 1: itemIcon = register.registerIcon("projectbench:pbupii");
		}
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
		if(world.isRemote)
			return false;
		if(type == 0 && world.getBlockId(x,y,z) == Block.workbench.blockID && world.getBlockTileEntity(x, y, z) == null)
		{
			if(!player.capabilities.isCreativeMode)
				player.inventory.consumeInventoryItem(ProjectBench.instance.projectBenchUpgrade.itemID);
			world.setBlock(x, y, z, ProjectBench.instance.projectBench.blockID, 0, 3);
			world.setBlockTileEntity(x, y, z, new TileEntityProjectBench());
			world.setBlock(x, y, z, ProjectBench.instance.projectBench.blockID, 0, 3);
			world.markBlockForUpdate(x, y, z);
			
			return true;
		}else if(type == 1 && world.getBlockId(x,y,z) == ProjectBench.instance.projectBench.blockID && world.getBlockMetadata(x,y,z) == 0){
			if(world.getBlockTileEntity(x,y,z) == null)
				return false;
			if(!player.capabilities.isCreativeMode)
				player.inventory.consumeInventoryItem(ProjectBench.instance.projectBenchUpgradeII.itemID);
			TileEntityProjectBench tpb = (TileEntityProjectBench)world.getBlockTileEntity(x , y, z);
			tpb.emptyCraftingMatrix();
			TEProjectBenchII tpbII = new TEProjectBenchII();
			ItemStack stack1 = null;
			ItemStack stack2 = null;
			tpbII.initSlots = true;
			tpb.initSlots = true;
			for(int i = 0; i < tpbII.supplyMatrixSize; i++){
				stack2 = null;
				stack1 = tpb.getStackInSlot(i + tpb.getSupplyMatrixStart());
				if(stack1 != null)
					stack2 = ItemStack.copyItemStack(stack1);
				tpbII.setInventorySlotContents(i +tpbII.inventoryStart, stack2);
				tpb.setInventorySlotContents(i +9, null);
			}
			tpbII.initSlots = false;
			tpb.initSlots = false;
			tpbII.setDirection((byte)getPlayerFacing(player));
			world.setBlock(x, y, z, ProjectBench.instance.projectBench.blockID, 1, 3);
			world.setBlockTileEntity(x, y, z, null);
			world.setBlockTileEntity(x, y, z, tpbII);
			world.setBlock(x, y, z, ProjectBench.instance.projectBench.blockID, 1, 3);
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
    }
	
	public int getPlayerFacing(EntityPlayer player){
        byte dir = 0;
        int plyrFacing = MathHelper.floor_double(((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        if (plyrFacing == 0)
            dir = 2;
        if (plyrFacing == 1)
            dir = 5;
        if (plyrFacing == 2)
            dir = 3;
        if (plyrFacing == 3)
            dir = 4;
        return dir;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return this.getUnlocalizedName() + itemNames[type];
	}
}
