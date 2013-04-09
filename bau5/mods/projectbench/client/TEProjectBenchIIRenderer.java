package bau5.mods.projectbench.client;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TEProjectBenchII;
import cpw.mods.fml.client.FMLClientHandler;

public class TEProjectBenchIIRenderer extends TileEntitySpecialRenderer {
	
	private RenderItem renderItems;
	private Minecraft mc;
	
	private ArrayList<ItemStack> itemList;
	
	private boolean RENDER_ITEMS = ProjectBench.II_DO_RENDER;
	
	public TEProjectBenchIIRenderer()
	{
		mc = FMLClientHandler.instance().getClient();
		renderItems  = new RenderItem() {
			public byte getMiniItemCountForItemStack(ItemStack stack) { return 1; }
			public byte getMiniBlockCountForItemStack(ItemStack stack){ return 1; }
			public boolean shouldBob() { return false; }
			public boolean shouldSpreadItems() { return false; }
		};
		renderItems.setRenderManager(RenderManager.instance);	
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		if(te == null || !(te instanceof TEProjectBenchII))
			return;
		if(!RENDER_ITEMS || te.worldObj.getBlockId((int)x, (int)y, (int)z) != 0) 
			return;
		TEProjectBenchII tpb = (TEProjectBenchII)te;
		itemList = tpb.getDisplayList();
		ItemStack newStack = null;
		if(itemList == null || itemList.size() == 0)
			return;
		EntityItem ei = new EntityItem(tpb.worldObj);
		ei.hoverStart = 0f;
		
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		glPushMatrix();
		glEnable(32826 /* rescale */);
		glTranslatef((float) x, (float) y, (float) z);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 170F, 170F);
		glTranslatef(0F, 1.1F, 0F);
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		glScalef(0.4F, 0.4F, 0.4F);
		

		float xShift, yShift, zShift;
		xShift = yShift = zShift = 0F;
		zShift = 0.4F;
		for(int i = 0; i < itemList.size(); i++){
			if(i > 46)
				break;
			newStack = itemList.get(i).copy();
			newStack.stackSize = 1;
			xShift += 0.3F;
			if(i == 7 || i == 15 || i == 23 || i == 31 || i == 39){
				zShift += 0.4F;
				xShift = 0.2F;
			}
			ei.setEntityItemStack(newStack);
			glPushMatrix();
			glTranslatef(xShift, yShift, zShift);
			glPushMatrix();
			if(newStack.itemID <= Block.blocksList.length &&
					Block.blocksList[newStack.itemID] != null)
				glScalef(0.1f, 0.1f, 0.1f);
			renderItems.doRenderItem(ei, 0, 0, 0, 0, 0);
			glPopMatrix();
			glPopMatrix();
		}

		glDisable(32826 /* scale */);
		glPopMatrix();
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
