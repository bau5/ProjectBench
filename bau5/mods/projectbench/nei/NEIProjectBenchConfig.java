package bau5.mods.projectbench.nei;

import bau5.mods.projectbench.client.ProjectBenchGui;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEIProjectBenchConfig implements IConfigureNEI {
	@Override
	public void loadConfig() {

		API.registerGuiOverlay(ProjectBenchGui.class, "crafting",5,11);
        API.registerGuiOverlayHandler(ProjectBenchGui.class, new DefaultOverlayHandler(5,11), "crafting");
	}

	@Override
	public String getName() {
		return "Project Bench plugin";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

}
