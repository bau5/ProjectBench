package com.bau5.projectbench.client;

import com.bau5.projectbench.common.ProjectBench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ProjectBenchRenderer extends TileEntitySpecialRenderer {

    private RenderItem renderItems;

    public ProjectBenchRenderer(){
        renderItems = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void renderTileEntityAt(TileEntity p_180535_1_, double posX, double posZ, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
        TileEntityProjectBench tile = (TileEntityProjectBench)p_180535_1_;
        if(tile.getResult() != null) {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            float light = tile.getWorld().getLightFromNeighbors(tile.getPos().add(0, 1, 0));
            float rotation = Minecraft.getSystemTime() / 3000.0F * 300.0F;

            GL11.glTranslated(posX, posZ, p_180535_6_);
            GL11.glTranslated(0.5, 1.0, 0.5);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F * (light / 15), 170F * (light / 15));

            GL11.glScalef(0.4F, 0.4F, 0.4F);
            GL11.glTranslated(0, 0.5, 0);
            GL11.glRotatef(rotation / 7, 0F, 1.0F, 0F);
            GL11.glTranslatef(0.0F, (float)(.1 * Math.sin((double)rotation/300)), 0.0F);
            renderItems.renderItemModel(tile.getResult());
            GL11.glPopMatrix();
        }
    }
}
