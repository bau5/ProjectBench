package bau5.mods.projectbench.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glPopMatrix;

import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class TEProjectBenchRenderer extends TileEntitySpecialRenderer 
{
	private RenderBlocks renderBlocks;
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
										 double z, float partialTick) 
	{
		glPushMatrix();
		if(te == null || !(te instanceof TileEntityProjectBench))
		{
			return;
		}
		TileEntityProjectBench tpb = (TileEntityProjectBench)te;
		renderBlocks = new RenderBlocks(te.worldObj);
		renderBlocks.renderBlockByRenderType(ProjectBench.instance.projectBench, 
														 (int)x, (int)y, (int)z);
		ItemStack craftingResult = tpb.findRecipe();
		if(craftingResult != null)
		{
			glPushMatrix();
			glColor4f(1F, 1F, 1F, 1F);
			glPopMatrix();
			glPushMatrix();
			glDisable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glTranslatef((float) x, (float) y, (float) z);
			EntityItem customItem = new EntityItem(tileEntityRenderer.worldObj);
			customItem.posX = x;
			customItem.posY = y + 1;
			customItem.posZ = z;
			glPushMatrix();
			glTranslatef(0.5F, 1.2F, 0.5F);
			glScalef(0.3F, 0.3F, 0.3F);
			glPushMatrix();
			float rotation = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
			
			glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(craftingResult, IItemRenderer.ItemRenderType.ENTITY);
			if(customRenderer != null)
			{
				customItem.item = craftingResult;
				bindTextureByName("/terrain.png");
				overrideTexture(craftingResult.getItem());
				customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, craftingResult, renderBlocks, customItem);
			} else if(craftingResult.itemID < Block.blocksList.length && Block.blocksList[craftingResult.itemID] != null
					&& Block.blocksList[craftingResult.itemID].blockID != 0 
					&& RenderBlocks.renderItemIn3d(Block.blocksList[craftingResult.itemID].getRenderType()))
			{
				bindTextureByName("/terrain.png");
				overrideTexture(Block.blocksList[craftingResult.itemID]);
				renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], craftingResult.getItemDamage(), 1.0F);
			} else
			{
				int i = craftingResult.getIconIndex();
				if (craftingResult.itemID >= Block.blocksList.length || Block.blocksList[craftingResult.itemID] == null || Block.blocksList[craftingResult.itemID].blockID == 0) {
					bindTextureByName("/gui/items.png");
					overrideTexture(Item.itemsList[craftingResult.itemID]);
				} else {
					bindTextureByName("/terrain.png");
					overrideTexture(Block.blocksList[craftingResult.itemID]);
				}
				Tessellator tessellator = Tessellator.instance;
				float f5 = (float) ((i % 16) * 16 + 0) / 256F;
				float f8 = (float) ((i % 16) * 16 + 16) / 256F;
				float f10 = (float) ((i / 16) * 16 + 0) / 256F;
				float f12 = (float) ((i / 16) * 16 + 16) / 256F;
				float f13 = 1.0F;
				float f14 = 0.5F;
				float f15 = 0.25F;
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(0.0F - f14, 0.0F - f15, 0.0D, f5, f12);
				tessellator.addVertexWithUV(f13 - f14, 0.0F - f15, 0.0D, f8, f12);
				tessellator.addVertexWithUV(f13 - f14, 1.0F - f15, 0.0D, f8, f10);
				tessellator.addVertexWithUV(0.0F - f14, 1.0F - f15, 0.0D, f5, f10);
				tessellator.draw();
				glScalef(-1.0F, 1.0F, 1.0F);
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(0.0F - f14, 0.0F - f15, 0.0D, f5, f12);
				tessellator.addVertexWithUV(f13 - f14, 0.0F - f15, 0.0D, f8, f12);
				tessellator.addVertexWithUV(f13 - f14, 1.0F - f15, 0.0D, f8, f10);
				tessellator.addVertexWithUV(0.0F - f14, 1.0F - f15, 0.0D, f5, f10);
				tessellator.draw();
			}
			glPopMatrix();
			glPopMatrix();
			glPopMatrix();
		}
		
		glDisable(GL12.GL_RESCALE_NORMAL);
		glEnable(GL11.GL_LIGHTING);
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		glPopMatrix();
	}
	//Thank you CPW <3
	private void overrideTexture(Object obj) 
	{
		if (obj instanceof Item) 
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(((Item) obj).getTextureFile()));
		} else if (obj instanceof Block) 
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture(((Block) obj).getTextureFile()));
		}
	}

}
