package bau5.mods.projectbench.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.projectbench.common.CommonProxy;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderInformation() 
	{
		MinecraftForgeClient.preloadTexture(ProjectBench.textureFile);
		RenderingRegistry.registerBlockHandler(getPBRenderID(), new PBWorldRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, 
													 new TEProjectBenchRenderer());
		
	}
	public int getPBRenderID()
	{
		return ProjectBench.instance.pbRenderID;
	}
}
