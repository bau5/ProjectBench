package bau5.mods.projectbench.client;

import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import bau5.mods.projectbench.common.CommonProxy;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void registerRenderInformation()
	{
		MinecraftForgeClient.preloadTexture(ProjectBench.textureFile);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, 
													 new TEProjectBenchRenderer());
	}
} 