package bau5.mods.projectbench.common;

import bau5.mods.projectbench.client.ProjectBenchGui;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.client.FMLClientHandler;


//TODO Implement this...correctly. heh
public class NEIProjectBenchConfig implements IConfigureNEI
{

	@Override
	public void loadConfig() 
	{
		API.registerGuiOverlay(ProjectBenchGui.class, "crafting", 0, 20);
	}

	@Override
	public String getName() 
	{
		return "bau5_ProjectBench";
	}

	@Override
	public String getVersion() 
	{
		return "1.5";
	}

}
