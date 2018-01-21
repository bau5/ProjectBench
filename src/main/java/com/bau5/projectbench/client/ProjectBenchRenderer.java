package com.bau5.projectbench.client;

import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.utils.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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
        renderItems = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TileEntityProjectBench tile = (TileEntityProjectBench)te;
        if(render && tile.getResult() != null) {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            float light = tile.getWorld().getLightFromNeighbors(tile.getPos().add(0, 1, 0));
            float rotation = 360.0f * (((float)(Minecraft.getSystemTime() % (7200 * Config.ROTATION_SPEED))) / (7200.0f * (float)Config.ROTATION_SPEED));

            GL11.glTranslated(x + 0.5, y + 0.6 + render_height, z + 0.5);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F * (light / 15), 170F * (light / 15));

            if(spin)
                GL11.glRotatef(rotation, 0F, 1.0F, 0F);
            // TODO: enable bounce again
            // if(bounce)
            //     GL11.glTranslatef(0.0F, (float)((.1*bounce_height) * Math.sin((double)rotation/(360 * bounce_speed))), 0.0F);
            renderItems.renderItem(tile.getResult(), ItemCameraTransforms.TransformType.GROUND);
            GL11.glPopMatrix();
        }
    }
}
