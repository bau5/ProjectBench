package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.SimpleMessage;
import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.inventory.ContainerProjectBench;
import com.bau5.projectbench.common.utils.PlanHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bau5 on 4/15/2015.
 */
public class GuiProjectBench extends GuiContainer {
    private TileEntityProjectBench tile;
    private static final ResourceLocation gui_texture = new ResourceLocation("projectbench", "textures/gui/pbgui2.png");
    private static final ResourceLocation other_texture = new ResourceLocation("projectbench", "textures/gui/parts.png");

    public GuiProjectBench(InventoryPlayer inventory, TileEntityProjectBench tileEntity) {
        super(new ContainerProjectBench(inventory, tileEntity));
        tile = tileEntity;
        ySize += 60;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new Button(0, guiLeft + 10, guiTop + 56, "v"));
        buttonList.add(new Button(1, guiLeft + 11, guiTop + 20));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("Project Bench", 8, 6, 4210752);
        for(Object obj : buttonList){
            if(obj instanceof Button) {
                ((Button)obj).drawButtonToolTip(-20, 50);
            }
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            for(int i = 0; i < inventorySlots.inventorySlots.size(); i++){
                Slot slot = inventorySlots.inventorySlots.get(i);
                ItemStack stack = slot.getStack();
                if(!stack.isEmpty() && stack.getItem().equals(ProjectBench.plan) && stack.getMetadata() == 1){
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(768, 1);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    RenderHelper.disableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();

                    FontRenderer font = stack.getItem().getFontRenderer(stack);
                    if (font == null)
                        font = fontRenderer;
                    GlStateManager.colorMask(true, true, true, false);
                    int x = slot.xPos;
                    int y = slot.yPos;
                    ItemStack out = PlanHelper.getPlanResult(stack);
                    this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
                    itemRender.renderItemAndEffectIntoGUI(out, x, y);
                    itemRender.renderItemOverlayIntoGUI(font, out, x, y, "");

                    GlStateManager.colorMask(true, true, true, true);
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
        GlStateManager.color(1.0F, 1.0F, 1.0f, 1.0F);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        int inventoryStart = 76;
        int inventorySize = ((tile.getSizeInventory() - 9) / 9) * 19;

        mc.getTextureManager().bindTexture(gui_texture);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, inventoryStart);

        if (tile.getSizeInventory() > 9) {
            for (int i = 0; i < inventorySize / 19; i++) {
                this.drawTexturedModalRect(k, l + inventoryStart + 19 * i, 0, inventoryStart, this.xSize, 19);
            }
        }

        this.drawTexturedModalRect(k, l + inventoryStart + inventorySize, 0, inventoryStart + 38, this.xSize, this.ySize);

        //Draw stacks for the plan
        drawPlanStacks(k, l);

        //Render Fluid parts into gui
        if(tile.getHasFluidUpgrade()) {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getTextureManager().bindTexture(other_texture);
            int xDisp = k + 148;
            int xSize = 22;
            int yDisp = l + 15;
            int ySize = 54;
            this.drawTexturedModalRect(xDisp, yDisp, 45, 0, xSize, ySize);
            //Render the fluid itself in between the layers.
            if(tile.getFluidInTank() != null){
                FluidStack fstack = tile.getFluidInTank();
                int amount = (fstack.amount / 1000) * 3;
                int bucketSize = 3;
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(fstack.getFluid().getStill(fstack).toString());

                drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3, sprite, xSize - 6, (amount < bucketSize * 4) ? amount : 12);

                if(amount > 12){
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 12, sprite, xSize - 6, (amount < bucketSize * 8) ? amount - 12 : 12);
                }
                if(amount > 24){
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 24, sprite, xSize - 6, (amount < bucketSize * 12) ? amount - 24 : 12);
                }
                if(amount > 36){
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 36, sprite, xSize - 6, (amount < bucketSize * 16) ? amount - 36 : 12);
                }

            }

            mc.getTextureManager().bindTexture(other_texture);
            this.drawTexturedModalRect(xDisp + 3, yDisp, 67, 0, xSize - 7, ySize - 4);
            //Tooltip if mouse is over tank
            if(tile.getFluidInTank() != null){
                if(mouseX > xDisp && mouseX < xDisp + xSize
                        && mouseY > yDisp && mouseY < yDisp + ySize){
                    FluidStack fstack = tile.getFluidInTank();
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(fstack.amount + "mb of " +fstack.getLocalizedName());
                    drawHoveringText(list, mouseX, mouseY);
                }
            }
        }
    }

    public void drawTexturedModelRectFromIconInverted(int x, int y, TextureAtlasSprite sprite, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, this.zLevel).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + width, y, this.zLevel).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(x+ width, y - height, this.zLevel).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(x, y - height, this.zLevel).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
    }

    private void drawPlanStacks(int xSize, int ySize){
        TileEntityProjectBench tpb = tile;
        NonNullList<ItemStack> stacks = PlanHelper.getComponentsForPlan(tpb.getPlan());
        if (stacks.size() > 0) {
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    ItemStack stack = stacks.get(index++);
                    // Early out if empty
                    if (stack.isEmpty()) {
                        continue;
                    }
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(768, 1);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    RenderHelper.disableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    int xLoc = (xSize + 30) + (j * 18);
                    int yLoc = (ySize + 17) + (i * 18);
                    FontRenderer font = stack.getItem().getFontRenderer(stack);
                    if (font == null)
                        font = fontRenderer;
                    GlStateManager.colorMask(true, true, true, false);
                    itemRender.renderItemAndEffectIntoGUI(stack, xLoc, yLoc);
                    itemRender.renderItemOverlayIntoGUI(font, stack, xLoc, yLoc, "");
                    this.drawGradientRect(xLoc, yLoc, xLoc + 16, yLoc + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.disableBlend();
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.popMatrix();
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
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
            if(id == 1) {
                ItemStack stack = tile.getPlan();
                this.enabled = tile.getResult() != ItemStack.EMPTY &&
                        stack != ItemStack.EMPTY && !stack.hasTagCompound() && stack.getCount() == 1;
            }
            super.drawButton(mc, mouseX, mouseY, partial);
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
                float prez = zLevel;
                zLevel = 400.0F;
                if (id == 0) {
                    drawHoveringText(Collections.singletonList("Empty Grid"), mouseX, mouseY);
                } else if (id == 1) {
                    drawHoveringText(Collections.singletonList("Write Plan"), mouseX, mouseY);
                }
                zLevel = prez;
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if(hovered){
                ProjectBench.network.sendToServer(new SimpleMessage(id, tile.getWorld().provider.getDimension(), tile.getPos()));
            }
        }
    }
}
