package bau5.mods.projectbench.client;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;

public class TEProjectBenchRenderer extends TileEntitySpecialRenderer  
{
	private RenderBlocks renderBlocks;
	private boolean RENDER_ITEM = ProjectBench.instance.DO_RENDER;

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
										 double z, float partialTick) 
	{
		if(te == null || !(te instanceof TileEntityProjectBench))
		{
			return;
		}
		TileEntityProjectBench tpb = (TileEntityProjectBench)te;
		renderBlocks = new RenderBlocks(tpb.worldObj);
		renderBlocks.renderBlockByRenderType(ProjectBench.instance.projectBench, 
														 (int)x, (int)y, (int)z);
		
		if(RENDER_ITEM && tpb.worldObj.getBlockId(tpb.xCoord, tpb.yCoord + 1, tpb.zCoord) == 0 && tpb.worldObj.getClosestPlayer(tpb.xCoord, tpb.yCoord, tpb.zCoord, 15) != null)
		{
			ItemStack stack = tpb.getResult();
			if(stack == null)
				return;
			EntityItem ei = new EntityItem(tpb.worldObj);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glPushMatrix();
			glEnable(32826 /*rescale*/);
			glTranslatef((float)x,(float)y,(float)z);
			glTranslatef(0.5F, 1.2F, 0.5F);
			glScalef(0.3F, 0.3F, 0.3F);

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F, 170F);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			float var9 = (float)(Minecraft.getSystemTime()) / (3000.0F) * 256.0F;
            
			glRotatef(var9 / 5, 0, 1.0F, 0);
			IItemRenderer customItemRenderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);
			if(customItemRenderer != null)
			{
				glPushMatrix();
				glTranslatef(0, 0.25F, 0);
				glScalef(0.8F, 0.8F, 0.8F);
				ForgeHooksClient.bindTexture(stack.getItem().getTextureFile(), 0);
				ei.item = stack;
				customItemRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, stack, renderBlocks, ei);
				glPopMatrix();
			} else if(stack.itemID < Block.blocksList.length && Block.blocksList[stack.itemID] != null
					  && Block.blocksList[stack.itemID].blockID != 0)
			{
				glPushMatrix();
				ForgeHooksClient.bindTexture(stack.getItem().getTextureFile(), 0);
				renderBlocks.renderBlockAsItem(Block.blocksList[stack.itemID], stack.getItemDamage(), 1.0F);
				glPopMatrix();
			} else
			{
				glPushMatrix();
				glScalef(1.2F, 1.2F, 1.2F);
				ForgeHooksClient.bindTexture(stack.getItem().getTextureFile(), 0);
				int index = stack.getIconIndex();
				Tessellator tessellator = Tessellator.instance;
				float f5 = (float) ((index % 16) * 16 + 0) / 256F;
				float f8 = (float) ((index % 16) * 16 + 16) / 256F;
				float f10 = (float) ((index / 16) * 16 + 0) / 256F;
				float f12 = (float) ((index / 16) * 16 + 16) / 256F;
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(0.0F - 0.5F, 0.0F - 0.25F, 0.0D, f5, f12);
				tessellator.addVertexWithUV(1.0F - 0.5F, 0.0F - 0.25F, 0.0D, f8, f12);
				tessellator.addVertexWithUV(1.0F - 0.5F, 1.0F - 0.25F, 0.0D, f8, f10);
				tessellator.addVertexWithUV(0.0F - 0.5F, 1.0F - 0.25F, 0.0D, f5, f10);
				tessellator.draw();
				glScalef(-1.0F, 1.0F, 1.0F);
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(0.0F - 0.5F, 0.0F - 0.25F, 0.0D, f5, f12);
				tessellator.addVertexWithUV(1.0F - 0.5F, 0.0F - 0.25F, 0.0D, f8, f12);
				tessellator.addVertexWithUV(1.0F - 0.5F, 1.0F - 0.25F, 0.0D, f8, f10);
				tessellator.addVertexWithUV(0.0F - 0.5F, 1.0F - 0.25F, 0.0D, f5, f10);
				tessellator.draw();
				glPopMatrix();
			}
			glDisable(32826 /* scale */);
			glPopMatrix();
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
		