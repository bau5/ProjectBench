package bau5.mods.projectbench.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.projectbench.client.ProjectBenchGui;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * CommonProxy
 * 
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class CommonProxy implements IGuiHandler
{
	public void registerRenderInformation()	{ }

	@Override
	public Object getServerGuiElement(int ID,
			EntityPlayer player,
			World world, int x, int y, int z) {

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityProjectBench && ID == 0)
			return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)te);
		if(te != null && te instanceof TEProjectBenchII && ID == 1)
			return new ContainerProjectBenchII(player.inventory, (TEProjectBenchII)te);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		TileEntity te = world.getBlockTileEntity(x,y,z);
		if(te != null && (te instanceof TileEntityProjectBench || te instanceof TEProjectBenchII))
			return new ProjectBenchGui(player.inventory, te, ID);
		return null;
	}
	
	public net.minecraft.world.World getClientSideWorld()
	{
		return null;
	}

}
