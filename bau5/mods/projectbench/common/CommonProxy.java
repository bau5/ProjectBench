package bau5.mods.projectbench.common;

import bau5.mods.projectbench.client.ProjectBenchGui;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderInformation()	{ }

	@Override
	public Object getServerGuiElement(int ID,
			net.minecraft.entity.player.EntityPlayer player,
			net.minecraft.world.World world, int x, int y, int z) {
		net.minecraft.tileentity.TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityProjectBench)
		{ 
			switch(ID)
			{
			case 0: return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)te);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID,
			net.minecraft.entity.player.EntityPlayer player,
			net.minecraft.world.World world, int x, int y, int z) {
		
		net.minecraft.tileentity.TileEntity te = world.getBlockTileEntity(x,y,z);
		TileEntityProjectBench tpb = null;
		if(te != null && te instanceof TileEntityProjectBench)
			tpb = (TileEntityProjectBench) te;
		if(tpb == null)
			return null;
		switch(ID)
		{
		case 0: return new ProjectBenchGui(player.inventory, tpb);
		}
		return null;
	}
	
	public net.minecraft.world.World getClientSideWorld()
	{
		return null;
	}

}
