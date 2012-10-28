package bau5.mods.projectbench.client;

import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.projectbench.common.CommonProxy;
import bau5.mods.projectbench.common.ProjectBench;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderInformation() 
	{
		MinecraftForgeClient.preloadTexture(ProjectBench.textureFile);
	}
}
