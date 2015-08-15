package com.bau5.projectbench.client;

import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.utils.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ProjectBenchRenderer extends TileEntitySpecialRenderer {

    private RenderItem renderItems;

    private static boolean render = Config.RENDER_ITEM;
    private static boolean spin = Config.RENDER_SPIN;
    private static boolean bounce = Config.RENDER_BOUNCE;

    private static double bounce_height = Config.BOUNCE_HEIGHT;
    private static double bounce_speed = Config.BOUNCE_SPEED;
    private static double render_height = Config.RENDER_HEIGHT;

    public ProjectBenchRenderer(){
        renderItems  = new RenderItem() {
            @Override
            public byte getMiniBlockCount(ItemStack stack, byte original) {
                return super.getMiniBlockCount(stack, original);
            }

            @Override
            public byte getMiniItemCount(ItemStack stack, byte original) {
                return super.getMiniItemCount(stack, original);
            }
            @Override
            public boolean shouldBob() { return false; }
            @Override
            public boolean shouldSpreadItems() { return false; }
        };
        renderItems.setRenderManager(RenderManager.instance);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float p_147500_8_) {
        TileEntityProjectBench tile = (TileEntityProjectBench)tileEntity;
        if(render && tile.getResult() != null && tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord + 1, tile.zCoord) == Blocks.air) {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            GL11.glTranslated(x, y, z);
            GL11.glTranslated(0.5, 0.7, 0.5);

            float light = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 15);
            float rotation = Minecraft.getSystemTime() / 3000.0F * 300.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light /*170F * (light / 255)*/);
            GL11.glTranslated(0, render_height, 0);
            if (spin)
                GL11.glRotatef(rotation / (float) Config.ROTATION_SPEED, 0F, 1.0F, 0F);
            if (bounce)
                GL11.glTranslatef(0.0F, (float) ((.05 * bounce_height) * Math.sin((double) rotation / (300 * bounce_speed))), 0.0F);
            EntityItem ei = new EntityItem(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
            ei.hoverStart = 0f;
            ei.setEntityItemStack(tile.getResult());
            GL11.glScalef(0.7F, 0.7F, 0.7F);
            renderItems.doRender(ei, 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
        }
    }
}
