package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ContainerProjectBench;
import com.bau5.projectbench.common.PlanHelper;
import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.SimpleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

/**
 * Created by bau5 on 4/15/2015.
 */
public class GuiProjectBench extends GuiContainer {
    private TileEntity tile;
    private static final ResourceLocation gui_texture = new ResourceLocation("projectbench", "textures/gui/pbGUI.png");


    public GuiProjectBench(InventoryPlayer inventory, TileEntity tileEntity) {
        super(new ContainerProjectBench(inventory, (TileEntityProjectBench) tileEntity));
        tile = tileEntity;
        ySize += 48;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new Button(0, guiLeft + 10, guiTop + 56, "v"));
        //TODO Plan
        buttonList.add(new Button(1, guiLeft + 10, guiTop + 20));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Project Bench", 8, 6, 4210752);
        for(Object obj : buttonList){
            if(obj instanceof Button) {
                ((Button)obj).drawButtonToolTip(-20, 50);
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            for(int i = 0; i < inventorySlots.inventorySlots.size(); i++){
                Slot slot = (Slot)inventorySlots.inventorySlots.get(i);
                ItemStack stack = slot.getStack();
                if(stack != null && stack.getItem().equals(ProjectBench.plan) && stack.getMetadata() == 1){
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(768, 1);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    RenderHelper.disableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    FontRenderer font = null;
                    if (stack != null) {
                        font = stack.getItem().getFontRenderer(stack);
                        if (font == null)
                            font = fontRendererObj;
                        GlStateManager.colorMask(true, true, true, false);
                        int x = slot.xDisplayPosition;
                        int y = slot.yDisplayPosition;
                        ItemStack out = PlanHelper.getPlanResult(stack);
                        this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
                        itemRender.renderItemAndEffectIntoGUI(out, x, y);
                        itemRender.renderItemOverlayIntoGUI(font, out, x, y, "");
                        GlStateManager.colorMask(true, true, true, true);
                    }
                    GlStateManager.disableBlend();
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //TODO hold shift, makes plans display result over the top of them.
        GlStateManager.color(1.0F, 1.0F, 1.0f, 1.0F);
        mc.getTextureManager().bindTexture(gui_texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize + 10);
        drawPlanStacks(k, l);
    }

    private void drawPlanStacks(int xSize, int ySize){
        TileEntityProjectBench tpb = (TileEntityProjectBench) tile;
        if (tpb.getResult() != null && tpb.getPlan() != null) {
            ItemStack[] stacks = PlanHelper.getComponentsForPlan(tpb.getPlan());
            if (stacks != null) {
                int index = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(768, 1);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableDepth();
                        RenderHelper.disableStandardItemLighting();
                        RenderHelper.enableGUIStandardItemLighting();
                        ItemStack stack = stacks[index++];
                        int xLoc = (xSize + 30) + (j * 18);
                        int yLoc = (ySize + 17) + (i * 18);
                        FontRenderer font = null;
                        if (stack != null) {
                            font = stack.getItem().getFontRenderer(stack);
                            if (font == null)
                                font = fontRendererObj;
                            GlStateManager.colorMask(true, true, true, false);
                            itemRender.renderItemAndEffectIntoGUI(stack, xLoc, yLoc);
                            itemRender.renderItemOverlayIntoGUI(font, stack, xLoc, yLoc, "");
                            this.drawGradientRect(xLoc, yLoc, xLoc + 16, yLoc + 16, -2130706433, -2130706433);
                            GlStateManager.colorMask(true, true, true, true);
                        }
                        GlStateManager.disableBlend();
                        GlStateManager.enableLighting();
                        GlStateManager.enableDepth();
                        RenderHelper.enableStandardItemLighting();
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    private class Button extends GuiButtonExt{
        private float hoverTime = 0;
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
                this.enabled = ((TileEntityProjectBench) tile).getResult() != null &&
                        stack != null && !stack.hasTagCompound() && stack.stackSize == 1;
            }
            super.drawButton(mc, mouseX, mouseY);
            if(getHoverState(isMouseOver()) == 2){
                hoverTime++;
            }else{
                if(hoverTime > 0){
                    hoverTime = 0;
                }
            }
        }

        public void drawButtonToolTip(int mouseX, int mouseY){
            if(hoverTime > 40) {
                ArrayList list = new ArrayList();
                zLevel = 400.0F;
                if (id == 0) {
                    list.add("Empty Grid");
                    drawHoveringText(list, mouseX, mouseY);
                } else if (id == 1) {
                    list.add("Write Plan");
                    drawHoveringText(list, mouseX, mouseY);
                }
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if(hovered){
                ProjectBench.network.sendToServer(new SimpleMessage(id, tile.getWorld().provider.getDimensionId(), tile.getPos()));
            }
        }
    }
}
