package bau5.mods.projectbench.client;

import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PBWorldRenderer implements ISimpleBlockRenderingHandler
{

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderBlocks) 
	{
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderBlocks) 
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if(entity instanceof TileEntityProjectBench)
		{
			renderBlocks.renderStandardBlock(block, x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() 
	{
		return false;
	}

	@Override
	public int getRenderId() 
	{
		return ProjectBench.instance.pbRenderID;
	}

}
