package bau5.mods.projectbench.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import bau5.mods.projectbench.client.ProjectBenchGui;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderInformation()	{ }
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) 
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
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
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) 
	{
		TileEntity te = world.getBlockTileEntity(x,y,z);
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
	
	public World getClientSideWorld()
	{
		return null;
	}

}
