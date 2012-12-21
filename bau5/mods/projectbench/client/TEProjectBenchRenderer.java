package bau5.mods.projectbench.client;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.Random;

import cpw.mods.fml.client.FMLClientHandler;

import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TEProjectBenchRenderer extends TileEntitySpecialRenderer {
	private RenderBlocks renderBlocks;
	private RenderItem   renderItems;
	private Minecraft	 mc;
	private Random  	 rand;
	private float		 previousRotation = 0F;
	private float		 stallPoint = 0F;
	
	private boolean RENDER_ITEM = ProjectBench.instance.DO_RENDER;
	private boolean ALT_RENDER  = ProjectBench.instance.DO_ALT_RENDER;
	private int SPEED_FACTOR = ProjectBench.instance.SPEED_FACTOR;

	public TEProjectBenchRenderer()
	{
		rand = new Random();
		mc = FMLClientHandler.instance().getClient();
		renderBlocks = new RenderBlocks();
		renderItems  = new RenderItem() {
			public byte getMiniItemCountForItemStack(ItemStack stack) { return 1; }
			public byte getMiniBlockCountForItemStack(ItemStack stack){ return 1; }
			public boolean shouldBob() { return false; }
			public boolean shouldSpreadItems() { return false; }
		};
		renderItems.setRenderManager(RenderManager.instance);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		if (te == null || !(te instanceof TileEntityProjectBench)) {
			return;
		}
		int count = 0;
		TileEntityProjectBench tpb = (TileEntityProjectBench) te;
		try{
			renderBlocks.renderBlockByRenderType(
					ProjectBench.instance.projectBench, (int) x, (int) y, (int) z);
		} catch (NullPointerException ex)
		{
			count++;
			if(count > 2) throw ex;
			renderBlocks = new RenderBlocks(tpb.worldObj);
		}

		if (RENDER_ITEM && tpb.worldObj.getBlockId(tpb.xCoord, tpb.yCoord + 1, tpb.zCoord) == 0
						&& tpb.worldObj.getClosestPlayer(tpb.xCoord, tpb.yCoord, tpb.zCoord, 15) != null
						&& !mc.isGamePaused) {
			ItemStack stack = tpb.getResult();
			if (stack == null)
				return;
			EntityItem ei = new EntityItem(tpb.worldObj);
			ei.hoverStart = 0f;
			ei.func_92013_a(stack);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glPushMatrix();
			glEnable(32826 /* rescale */);
			glTranslatef((float) x, (float) y, (float) z);
			glTranslatef(0.5F, 1.2F, 0.5F);
			glScalef(0.3F, 0.3F, 0.3F);

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F, 170F);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			float rotational = (float) (Minecraft.getSystemTime()) / (3000.0F) * 300.0F;

			if(ei.func_92014_d() == null)
				return;
			if(ALT_RENDER)
			{
				glRotatef(rotational / SPEED_FACTOR, 0F, 1F, 0F);
				glScalef(2.5F, 2.5F, 2.5F);
				renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
			}else
			{
				
			}
			
			glDisable(32826 /* scale */);
			glPopMatrix();
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
