package bau5.mods.projectbench.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.ContainerProjectBench;
import bau5.mods.projectbench.common.ContainerProjectBenchII;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TEProjectBenchII;
import bau5.mods.projectbench.common.TileEntityProjectBench;

public class ProjectBenchGui extends GuiContainer {
	private int ID;
	private String texPath;
	public ProjectBenchGui(InventoryPlayer inventoryPlayer,
			TileEntity tileEntity, int guiID) {
		super((guiID == 0) ? new ContainerProjectBench(inventoryPlayer, (TileEntityProjectBench)tileEntity) 
							  : new ContainerProjectBenchII(inventoryPlayer, (TEProjectBenchII)tileEntity));
		ySize += 48;
		ID = guiID;
		if(ID == 0)
			texPath = ProjectBench.baseTexFile + "/gui/pbGUI.png";
		else if (ID == 1)
			texPath = ProjectBench.baseTexFile + "/gui/pbGUI2.png";
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if(ID == 0)
			fontRenderer.drawString("Project Bench", 8, 6, 4210752);
		else if(ID == 1)
			fontRenderer.drawString("Project Bench Mk. II", 8, 4, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		mc.renderEngine.bindTexture(texPath);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}