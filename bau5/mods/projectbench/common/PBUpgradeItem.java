package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PBUpgradeItem extends Item	
{
	public PBUpgradeItem(int id) 
	{
		super(id);
		setUnlocalizedName("pbUpgrade");
		setMaxStackSize(16);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		super.addInformation(stack, player, list, bool);
		list.add("\u00A77" + "Used to upgrade a Crafting Bench.");
	}
	
	@Override
	public void func_94581_a(IconRegister register) {
		iconIndex = register.func_94245_a("projectbench:pbup");
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
		if(world.isRemote)
			return false;
		if(world.getBlockId(x,y,z) == Block.workbench.blockID && world.getBlockTileEntity(x, y, z) == null)
		{
			if(!player.capabilities.isCreativeMode)
				player.inventory.consumeInventoryItem(ProjectBench.instance.projectBenchUpgrade.itemID);
			world.setBlockAndMetadataWithNotify(x, y, z, ProjectBench.instance.projectBench.blockID, 0, 3);
			world.setBlockTileEntity(x, y, z, new TileEntityProjectBench());
			world.setBlockAndMetadataWithNotify(x, y, z, ProjectBench.instance.projectBench.blockID, 0, 3);
			world.markBlockForUpdate(x, y, z);
			
			return true;
		}
		return false;
    }
}
