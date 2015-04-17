package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ContainerProjectBench;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Rick on 4/15/2015.
 */
public class GuiProjectBench extends GuiContainer{

    private static final ResourceLocation gui_texture = new ResourceLocation("projectbench", "textures/gui/pbGUI.png");

    public GuiProjectBench(InventoryPlayer inventory, TileEntity tileEntity) {
        super(new ContainerProjectBench(inventory, (TileEntityProjectBench)tileEntity));
    }

    @Override
    public void initGui() {
        ySize += 48;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Project Bench", 8, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0f, 1.0F);
        mc.getTextureManager().bindTexture(gui_texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize+10);
    }
}
