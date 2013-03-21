package bau5.mods.projectbench.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.ContainerProjectBench;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;

public class ProjectBenchGui extends GuiContainer {

	public ProjectBenchGui(InventoryPlayer inventoryPlayer,
			TileEntityProjectBench tileEntity) {
		super(new ContainerProjectBench(inventoryPlayer, tileEntity));
		ySize += 40;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString("Project Bench", 8, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		mc.renderEngine.func_98187_b(ProjectBench.baseTexFile
				+ "/gui/pbGUI.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}
