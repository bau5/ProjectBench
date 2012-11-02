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
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

public class TEProjectBenchRenderer extends TileEntitySpecialRenderer  
{
	private RenderBlocks renderBlocks;

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
		GL11.glPushMatrix();
//		GL11.glDisable(2896 /*GL_LIGHTING*/);
		renderResult(tpb, x, y, z);
//		GL11.glEnable(2896);
		GL11.glPopMatrix();
	}
	public void renderResult(TileEntityProjectBench tpb, double x, double y, double z)
	{
		ItemStack craftingResult = tpb.findRecipe();
		if(craftingResult == null)
			return;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
		renderBlock(craftingResult, tpb, x, y, z);
		
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}
	public void renderBlock(ItemStack craftingResult, TileEntityProjectBench tpb,
							double x, double y, double z)
	{
        GL11.glTranslatef(0.5F, 1.2F, 0.5F);
		float timeD = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
		glRotatef(timeD, 0.0F, 1.0F, 0.0F);

        GL11.glScalef(0.3F, 0.3F, 0.3F);
        this.bindTextureByName("/terrain.png");
        GL11.glPushMatrix();
        int var4 = (int) tpb.worldObj.getBrightness((int)x, (int)y, (int)z, tpb.worldObj.provider.lightBrightnessTable.length - 1);
       	int var5 = var4 % 65536;
        int var6 = var4 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
       
        this.renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], 
        										craftingResult.getItemDamage(), 15);
        GL11.glPopMatrix();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
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
//	bindTextureByName("/terrain.png");
//    this.overrideTexture(craftingResult);
//    GL11.glPushMatrix();
//	  GL11.glEnable(GL12.GL_RESCALE_NORMAL);
//	  GL11.glDisable(GL11.GL_LIGHTING);
//	    GL11.glPushMatrix();
//	      glTranslatef((float)x, (float)y, (float)z);
//	      glTranslatef(0.5F, 1.2F, 0.5F);
//	      glScalef(0.3F, 0.3F, 0.3F);
//	        glPushMatrix();
//              GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//              this.renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], craftingResult.getItemDamage(), 0.5F);
//            glPopMatrix();
//        GL11.glPopMatrix();
//      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//      GL11.glEnable(GL11.GL_LIGHTING);
//    GL11.glPopMatrix();
	
		//	glPushMatrix();
		//	glColor4f(1F, 1F, 1F, 1F);
		//	glPopMatrix();
		//	glPushMatrix();
		//	glDisable(GL11.GL_LIGHTING);
		//    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//	glTranslatef((float) x, (float) y, (float) z);
		//	EntityItem customItem = new EntityItem(tileEntityRenderer.worldObj);
		//	customItem.posX = x;
		//	customItem.posY = y + 1;
		//	customItem.posZ = z;
		//	glPushMatrix();
		//	glTranslatef(0.5F, 1.2F, 0.5F);
		//	glScalef(0.3F, 0.3F, 0.3F);
		//	glPushMatrix();
		//	float rotation = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
		//	
		//	glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		//	if(craftingResult.itemID < Block.blocksList.length && Block.blocksList[craftingResult.itemID] != null
		//			&& Block.blocksList[craftingResult.itemID].blockID != 0 
		//			&& RenderBlocks.renderItemIn3d(Block.blocksList[craftingResult.itemID].getRenderType()))
		//	{
		//		bindTextureByName("/terrain.png");
		//		overrideTexture(Block.blocksList[craftingResult.itemID]);
		//		renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], craftingResult.getItemDamage(), 0.0F);
		//	}
		//	IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(craftingResult, IItemRenderer.ItemRenderType.ENTITY);
		//	if(customRenderer != null)
		//	{
		//		customItem.item = craftingResult;
		//		bindTextureByName("/terrain.png");
		//		overrideTexture(craftingResult.getItem());
		//		customRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, craftingResult, renderBlocks, customItem);
		//	} else if(craftingResult.itemID < Block.blocksList.length && Block.blocksList[craftingResult.itemID] != null
		//			&& Block.blocksList[craftingResult.itemID].blockID != 0 
		//			&& RenderBlocks.renderItemIn3d(Block.blocksList[craftingResult.itemID].getRenderType()))
		//	{
		//		bindTextureByName("/terrain.png");
		//		overrideTexture(Block.blocksList[craftingResult.itemID]);
		//		renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], craftingResult.getItemDamage(), 1.0F);
		//	} else
		//	{
		//		int itemTextureIndex = craftingResult.getIconIndex();
		//		if (craftingResult.itemID >= Block.blocksList.length || Block.blocksList[craftingResult.itemID] == null || Block.blocksList[craftingResult.itemID].blockID == 0) {
		//			bindTextureByName("/gui/items.png");
		//			overrideTexture(Item.itemsList[craftingResult.itemID]);
		//		} else {
		//			bindTextureByName("/terrain.png");
		//			overrideTexture(Block.blocksList[craftingResult.itemID]);
		//		}
		//		
		//		Tessellator tessellator = Tessellator.instance;
		//		float f5 = (float) ((itemTextureIndex % 16) * 16 + 0) / 256F;
		//		float f8 = (float) ((itemTextureIndex % 16) * 16 + 16) / 256F;
		//		float f10 = (float) ((itemTextureIndex / 16) * 16 + 0) / 256F;
		//		float f12 = (float) ((itemTextureIndex / 16) * 16 + 16) / 256F;
		//		
		//		tessellator.startDrawingQuads();
		//		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		//		tessellator.addVertexWithUV(0.0F - 0.5F, 0.0F - 0.25F, 0.0D, f5, f12);
		//		tessellator.addVertexWithUV(1.0F - 0.5F, 0.0F - 0.25F, 0.0D, f8, f12);
		//		tessellator.addVertexWithUV(1.0F - 0.5F, 1.0F - 0.25F, 0.0D, f8, f10);
		//		tessellator.addVertexWithUV(0.0F - 0.5F, 1.0F - 0.25F, 0.0D, f5, f10);
		//		tessellator.draw();
		//		glScalef(-1.0F, 1.0F, 1.0F);
		//		tessellator.startDrawingQuads();
		//		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		//		tessellator.addVertexWithUV(0.0F - 0.5F, 0.0F - 0.25F, 0.0D, f5, f12);
		//		tessellator.addVertexWithUV(1.0F - 0.5F, 0.0F - 0.25F, 0.0D, f8, f12);
		//		tessellator.addVertexWithUV(1.0F - 0.5F, 1.0F - 0.25F, 0.0D, f8, f10);
		//		tessellator.addVertexWithUV(0.0F - 0.5F, 1.0F - 0.25F, 0.0D, f5, f10);
		//		tessellator.draw();
		//	}
		//	glPopMatrix();
		//	glPopMatrix();
		//	glPopMatrix();
		//}
		//
		//glDisable(GL12.GL_RESCALE_NORMAL);
		//glEnable(GL11.GL_LIGHTING);
		//glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//glPopMatrix();
