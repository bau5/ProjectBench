package bau5.mods.projectbench.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRotatef;

import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

public class TEProjectBenchRenderer extends TileEntitySpecialRenderer {

	private RenderBlocks renderBlocks;

	//Function that renders tile entity and the other block thing
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
										 double z, float partialTick) 
	{
		//Null check blah blah
		if(te == null || !(te instanceof TileEntityProjectBench))
		{
			return;
		}
		TileEntityProjectBench tpb = (TileEntityProjectBench)te;
		renderBlocks = new RenderBlocks(tpb.worldObj);
		//This renders the block itself
		renderBlocks.renderBlockByRenderType(ProjectBench.instance.projectBench, 
														 (int)x, (int)y, (int)z);
		int dirRotation = 0;
		switch(tpb.getDirectionFacing())
		{
		case 2: dirRotation = 90; break;
		case 3: dirRotation = -90; break;
		case 4: dirRotation = 180; break;
		case 5: dirRotation = 0; break;
		default: dirRotation = 0; break;
		}
		ItemStack stack = tpb.findRecipe();
		if(stack != null && tpb.worldObj.getBlockId((int)x, (int)y + 1, (int)z) == 0)
		{
			EntityItem ei = new EntityItem(tpb.worldObj);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glPushMatrix();
			glEnable(32826 /*rescale*/);
			glTranslatef((float)x,(float)y,(float)z);
			glTranslatef(0.5F, 1.2F, 0.5F);
			glScalef(0.3F, 0.3F, 0.3F);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200 / 1.0F, 200 / 1.0F);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			glRotatef(dirRotation, 0, 1.0F, 0);
			
			IItemRenderer customItemRenderer = MinecraftForgeClient.getItemRenderer(stack, ItemRenderType.ENTITY);
			
			if(customItemRenderer != null)
			{
				ForgeHooksClient.bindTexture(stack.getItem().getTextureFile(), 0);
				customItemRenderer.renderItem(ItemRenderType.ENTITY, stack, stack.getItemDamage());
			} else if(stack.itemID < Block.blocksList.length && Block.blocksList[stack.itemID] != null
					  && Block.blocksList[stack.itemID].blockID != 0)
			{
				glPushMatrix();
				ForgeHooksClient.bindTexture("/terrain.png", 0);
				glRotatef(30F, 0, 0, 1.0F);
				renderBlocks.renderBlockAsItem(Block.blocksList[stack.itemID], stack.getItemDamage(), 1.0F);
				glPopMatrix();
			} else
			{
				glPushMatrix();
				glRotatef(90F, 0, 1.0F, 0);
				glScalef(1.2F, 1.2F, 1.2F);
				ForgeHooksClient.bindTexture("/gui/items.png", 0);
				int i = stack.getIconIndex();
				Tessellator tessellator = Tessellator.instance;
				float f5 = (float) ((i % 16) * 16 + 0) / 256F;
				float f8 = (float) ((i % 16) * 16 + 16) / 256F;
				float f10 = (float) ((i / 16) * 16 + 0) / 256F;
				float f12 = (float) ((i / 16) * 16 + 16) / 256F;
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
