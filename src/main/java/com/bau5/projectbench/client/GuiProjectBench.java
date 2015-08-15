package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.SimpleMessage;
import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.inventory.ContainerProjectBench;
import com.bau5.projectbench.common.utils.PlanHelper;
import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;


/**
 * Created by bau5 on 4/15/2015.
 */
public class GuiProjectBench extends GuiContainer {

    private TileEntityProjectBench tile;
    private static final ResourceLocation gui_texture = new ResourceLocation("projectbench", "textures/gui/pbGUI.png");
    private static final ResourceLocation other_texture = new ResourceLocation("projectbench", "textures/gui/parts.png");

    public GuiProjectBench(InventoryPlayer inventory, TileEntityProjectBench tileEntity) {
        super(new ContainerProjectBench(inventory, tileEntity));
        tile = tileEntity;
        ySize += 48;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new Button(0, guiLeft + 10, guiTop + 56, "v"));
        buttonList.add(new Button(1, guiLeft + 11, guiTop + 20));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Project Bench", 8, 6, 4210752);
        for (Object obj : buttonList) {
            if (obj instanceof Button) {
                ((Button) obj).drawButtonToolTip(- 20, 50);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
                ItemStack stack = slot.getStack();
                if (stack != null && stack.getItem().equals(ProjectBench.plan) && stack.getItemDamage() == 1) {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(768, 1);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderHelper.disableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    FontRenderer font = null;
                    if (stack != null) {
                        font = stack.getItem().getFontRenderer(stack);
                        if (font == null) {
                            font = fontRendererObj;
                        }
                        int x = slot.xDisplayPosition;
                        int y = slot.yDisplayPosition;
                        ItemStack out = PlanHelper.getPlanResult(stack);
                        this.drawGradientRect(x, y, x + 16, y + 16, - 2130706433, - 2130706433);
                        float old = itemRender.zLevel;
                        itemRender.zLevel = 200;
                        itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), out, x, y);
                        itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), out, x, y, "");
                        itemRender.zLevel = old;
                    }
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
//                    RenderHelper.enableStandardItemLighting();
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0f, 1.0F);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        mc.getTextureManager().bindTexture(gui_texture);
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize + 10);
        //Draw stacks for the plan
        drawPlanStacks(k, l);
        //Render Fluid parts into gui
        if (tile.getHasFluidUpgrade()) {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getTextureManager().bindTexture(other_texture);
            int xDisp = k + 148;
            int xSize = 22;
            int yDisp = l + 15;
            int ySize = 54;
            this.drawTexturedModalRect(xDisp, yDisp, 45, 0, xSize, ySize);
            //Render the fluid itself in between the layers.
            if (tile.getFluidInTank() != null) {
                FluidStack fstack = tile.getFluidInTank();
                int amount = (fstack.amount / 1000) * 3;
                int bucketSize = 3;
                mc.getTextureManager().bindTexture(mc.getTextureManager().getResourceLocation(fstack.getFluid().getSpriteNumber()));
                drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3, fstack.getFluid().getIcon(), xSize - 6, (amount < bucketSize * 4) ? amount : 12);
                if (amount > 12) {
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 12, fstack.getFluid().getIcon(), xSize - 6, (amount < bucketSize * 8) ? amount - 12 : 12);
                }
                if (amount > 24) {
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 24, fstack.getFluid().getIcon(), xSize - 6, (amount < bucketSize * 12) ? amount - 24 : 12);
                }
                if (amount > 36) {
                    drawTexturedModelRectFromIconInverted(xDisp + 3, yDisp + ySize - 3 - 36, fstack.getFluid().getIcon(), xSize - 6, (amount < bucketSize * 16) ? amount - 36 : 12);
                }

            }

            mc.getTextureManager().bindTexture(other_texture);
            this.drawTexturedModalRect(xDisp + 3, yDisp, 67, 0, xSize - 7, ySize - 4);
            //Tooltip if mouse is over tank
            if (tile.getFluidInTank() != null) {
                if (mouseX > xDisp && mouseX < xDisp + xSize
                        && mouseY > yDisp && mouseY < yDisp + ySize) {
                    FluidStack fstack = tile.getFluidInTank();
                    ArrayList list = new ArrayList();
                    list.add(fstack.amount + "mb of " + fstack.getLocalizedName());
                    drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                }
            }
        }
    }

    public void drawTexturedModelRectFromIconInverted(int par1, int par2, IIcon icon, int par4, int par5) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) icon.getMinU(), (double) icon.getMaxV());
        tessellator.addVertexWithUV((double) (par1 + par4), (double) (par2 + 0), (double) this.zLevel, (double) icon.getMaxU(), (double) icon.getMaxV());
        tessellator.addVertexWithUV((double) (par1 + par4), (double) (par2 - par5), (double) this.zLevel, (double) icon.getMaxU(), (double) icon.getMinV());
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 - par5), (double) this.zLevel, (double) icon.getMinU(), (double) icon.getMinV());
        tessellator.draw();
    }

    private void drawPlanStacks(int xSize, int ySize) {
        TileEntityProjectBench tpb = tile;
        if (tpb.getResult() != null && tpb.getPlan() != null) {
            ItemStack[] stacks = PlanHelper.getComponentsForPlan(tpb.getPlan());
            if (stacks != null) {
                int index = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        ItemStack stack = stacks[index++];
                        int xLoc = (xSize + 30) + (j * 18);
                        int yLoc = (ySize + 17) + (i * 18);
                        FontRenderer font = null;
                        if (stack != null) {
                            GL11.glPushMatrix();
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glDisable(GL11.GL_LIGHTING);
                            GL11.glBlendFunc(768, 1);
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            font = stack.getItem().getFontRenderer(stack);
                            if (font == null) {
                                font = fontRendererObj;
                            }
                            itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, xLoc, yLoc);
                            itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, xLoc, yLoc, "");
                            this.drawGradientRect(xLoc, yLoc, xLoc + 16, yLoc + 16, - 2130706433, - 2130706433);
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_LIGHTING);
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }
    }

    private class Button extends GuiButtonExt {

        private float hoverTime = 0;

        public Button(int id, int x, int y, String disp) {
            super(id, x, y, 12, 12, disp);
        }

        public Button(int id, int x, int y) {
            super(id, x, y, 10, 10, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (id == 1) {
                ItemStack stack = tile.getStackInSlot(27);
                this.enabled = tile.getResult() != null &&
                        stack != null && ! stack.hasTagCompound() && stack.stackSize == 1;
            }
            super.drawButton(mc, mouseX, mouseY);
            if (getHoverState(isMouseOver(mouseX, mouseY)) == 2) {
                hoverTime++;
            } else {
                if (hoverTime > 0) {
                    hoverTime = 0;
                }
            }
        }

        public void drawButtonToolTip(int mouseX, int mouseY) {
            if (hoverTime > 40) {
                ArrayList list = new ArrayList();
                zLevel = 400.0F;
                if (id == 0) {
                    list.add("Empty Grid");
                    drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                } else if (id == 1) {
                    list.add("Write Plan");
                    drawHoveringText(list, mouseX, mouseY, fontRendererObj);
                }
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (getHoverState(isMouseOver(mouseX, mouseY)) == 2) {
                ProjectBench.network.sendToServer(new SimpleMessage(id, tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord));
            }
        }

        public boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX >= xPosition && mouseX <= xPosition + width
                    && mouseY >= yPosition && mouseY <= yPosition + height;
        }
    }
}
