package bau5.mods.projectbench.client;


import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderHelper;
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

		RenderHelper.disableStandardItemLighting();
		glColor4f(1.0F,1.0F, 1.0F, 1.0F);
		//Start OpenGL code
		GL11.glPushMatrix();
		
		renderResult(tpb, x, y, z); //encapsulated this function cause it's recommended 
		//enabling lighting again

		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();//pop first matrix, happens last.
	}
	private void renderResult(TileEntityProjectBench tpb, double x, double y, double z)
	{
		//The result, the mini block to be rendered
		ItemStack craftingResult = tpb.findRecipe();
		if(craftingResult == null)
			return;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);//Moving to block location
		GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/); //Enabling whatever this is
		renderMiniBlock(craftingResult, tpb, x, y, z);//encapsulated again
		
		GL11.glDisable(32826);//disable this thing
		GL11.glPopMatrix(); //pop second matrix
	}
	private void renderMiniBlock(ItemStack craftingResult, TileEntityProjectBench tpb,
							double x, double y, double z)
	{
		//Moving up a bit to see
        GL11.glTranslatef(0.5F, 1.2F, 0.5F);
		float timeD = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
		glRotatef(timeD, 0.0F, 1.0F, 0.0F); //spinning in circles

        GL11.glScalef(0.3F, 0.3F, 0.3F); //making smaller
        this.bindTextureByName("/terrain.png"); //texture bound

        GL11.glPushMatrix(); //new matrix
        //^from what i can tell does nothing
        //v passes the block off to be rendered by the typical handler
        renderBlocks.useInventoryTint = false;
        this.renderBlocks.renderBlockAsItem(Block.blocksList[craftingResult.itemID], 
        									  craftingResult.getItemDamage(), 1.0F);

        GL11.glPopMatrix(); //popping 3rd matrix
	}
}
		