package bau5.mods.projectbench.nei;

import net.minecraft.client.gui.inventory.GuiContainer;
import bau5.mods.projectbench.client.ProjectBenchGui;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;

public class NEIProjectBenchConfig implements IConfigureNEI, IOverlayHandler {
	@Override
	public void loadConfig() {
		API.registerGuiOverlay(ProjectBenchGui.class, "crafting", 5, 11);
        API.registerGuiOverlayHandler(ProjectBenchGui.class, new DefaultOverlayHandler(5, 11), "crafting");
        //If NEI is active, we need to change the method called in ProjectBenchGui!
        ProjectBenchGui.setIsNEIActive(true);
	}

	@Override
	public String getName() {
		return "bau5_PBPlugin";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe,
			int recipeIndex, boolean shift) {
		System.out.println("asdf");
		
	}
}