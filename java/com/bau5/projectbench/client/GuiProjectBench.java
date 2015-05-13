package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ContainerProjectBench;
import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.SimpleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

/**
 * Created by bau5 on 4/15/2015.
 */
public class GuiProjectBench extends GuiContainer{
    private TileEntity tile;
    private static final ResourceLocation gui_texture = new ResourceLocation("projectbench", "textures/gui/pbGUI.png");

    public GuiProjectBench(InventoryPlayer inventory, TileEntity tileEntity) {
        super(new ContainerProjectBench(inventory, (TileEntityProjectBench)tileEntity));
        tile = tileEntity;
    }

    @Override
    public void initGui() {
        ySize += 48;
        super.initGui();
        buttonList.add(new Button(0, guiLeft + 10, guiTop +56, "v"));
        //TODO Plan
//        buttonList.add(new Button(1, guiLeft + 10, guiTop +20));
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

    private class Button extends GuiButtonExt{
        public Button(int id, int x, int y, String disp){
            super(id, x, y, 12, 12, disp);
        }
        public Button(int id, int x, int y){
            super(id, x, y, 10, 10, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if(id == 1) {
                ItemStack stack = ((TileEntityProjectBench)tile).getStackInSlot(27);
                this.enabled = stack != null && !stack.hasTagCompound();
            }
            super.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if(hovered){
                ProjectBench.network.sendToServer(new SimpleMessage(id, tile.getWorld().provider.getDimensionId(), tile.getPos()));
            }
        }
    }
}
