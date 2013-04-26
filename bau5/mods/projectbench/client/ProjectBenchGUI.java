package bau5.mods.projectbench.client;

import invtweaks.api.ContainerGUI;
import invtweaks.api.ContainerSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.ContainerProjectBench;
import bau5.mods.projectbench.common.ContainerProjectBenchII;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TEProjectBenchII;
import bau5.mods.projectbench.common.TileEntityProjectBench;

/**
 * 
 * ProjectBenchGui
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

@ContainerGUI
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
	
	@ContainerGUI.ContainerSectionCallback
	public Map<ContainerSection, List<Slot>> getContainerSections(){
		Map<ContainerSection, List<Slot>> result = new HashMap<ContainerSection, List<Slot>>();
		if(inventorySlots instanceof ContainerProjectBenchII){
			result.put(ContainerSection.CHEST, inventorySlots.inventorySlots.subList(27, 45));
		}
		else if(inventorySlots instanceof ContainerProjectBench){
			result.put(ContainerSection.CRAFTING_OUT, inventorySlots.inventorySlots.subList(0, 1));
			result.put(ContainerSection.CRAFTING_IN_PERSISTENT, inventorySlots.inventorySlots.subList(1, 10));
			result.put(ContainerSection.CHEST, inventorySlots.inventorySlots.subList(10, 28));
		}
		return result;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if(ID == 0)
			fontRenderer.drawString("Project Bench", 8, 6, 4210752);
		else if(ID == 1)
			fontRenderer.drawString("Project Bench Mk. II", 8, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		mc.renderEngine.bindTexture(texPath);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, -2, xSize, ySize);
	}

}