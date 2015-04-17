package bau5.mods.projectbench.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import bau5.mods.projectbench.common.EntityCraftingFrameII;

/**
 * RenderCraftingFrameII
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class RenderCraftingFrameII extends Render {
	private final RenderBlocks renderBlocks = new RenderBlocks();
	private static final ResourceLocation resourceLocation = new ResourceLocation("textures/blocks/crafting_table_top.png");

	private Icon icon;
	
	@Override
	public void updateIcons(IconRegister ir) {
		icon = Block.workbench.getBlockTextureFromSide(1);
	}
	
	private void render(EntityCraftingFrameII entity, double x, double y,
			double z, float par8, float par9) {
		if(renderBlocks.blockAccess == null)
			renderBlocks.blockAccess = entity.worldObj;
		GL11.glPushMatrix();
        float f2 = (float)(entity.posX - x) - 0.5F;
        float f3 = (float)(entity.posY - y) - 0.5F;
        float f4 = (float)(entity.posZ - z) - 0.5F;
        int i = entity.xPosition + Direction.offsetX[entity.hangingDirection];
        int j = entity.yPosition;
        int k = entity.zPosition + Direction.offsetZ[entity.hangingDirection];
        float f5 = i - f2; 
        float f6 = j - f3;
        float f7 = k - f4;
        GL11.glTranslatef(i - f2, j - f3, k - f4);
        renderFrameItemAsBlock(entity);
        renderItemInFrame(entity);
        GL11.glPopMatrix();
	}
	
	private void renderItemInFrame(EntityCraftingFrameII entity) {
		ItemStack itemstack = entity.getDisplayedItem();

        if (itemstack != null)
        {
            EntityItem entityitem = new EntityItem(entity.worldObj, 0.0D, 0.0D, 0.0D, itemstack);
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.453125F * Direction.offsetX[entity.hangingDirection], -0.18F, -0.453125F * Direction.offsetZ[entity.hangingDirection]);
            GL11.glRotatef(180.0F + entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((-90 * entity.getRotation()), 0.0F, 0.0F, 1.0F);

            switch (entity.getRotation())
            {
                case 1:
                    GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
                    break;
                case 2:
                    GL11.glTranslatef(0.0F, -0.32F, 0.0F);
                    break;
                case 3:
                    GL11.glTranslatef(0.16F, -0.16F, 0.0F);
            }

            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;
            GL11.glPopMatrix();
        }		
	}
	
	private void renderFrameItemAsBlock(EntityItemFrame eif)
    {
        GL11.glPushMatrix();
        this.renderManager.renderEngine.func_110577_a(TextureMap.field_110575_b);
        GL11.glRotatef(eif.rotationYaw, 0.0F, 1.0F, 0.0F);
        Block block = Block.planks;
        float f = 0.0625F;
        float f1 = 0.75F;
        float f2 = f1 / 2.0F;
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (0.5F - f2 + 0.0625F), (0.5F - f2 + 0.0625F), (f * 0.5F), (0.5F + f2 - 0.0625F), (0.5F + f2 - 0.0625F));
        this.renderBlocks.setOverrideBlockTexture(Block.workbench.getBlockTextureFromSide(1));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        this.renderBlocks.clearOverrideBlockTexture();
        this.renderBlocks.unlockBlockBounds();
        GL11.glPopMatrix();
        this.renderBlocks.setOverrideBlockTexture(Block.planks.getIcon(1, 1));
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (0.5F - f2), (0.5F - f2), (f + 1.0E-4F), (f + 0.5F - f2), (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (0.5F + f2 - f), (0.5F - f2), (f + 1.0E-4F), (0.5F + f2), (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (0.5F - f2), (0.5F - f2), f, (0.5F + f2), (f + 0.5F - f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks.overrideBlockBounds(0.0D, (0.5F - f2), (0.5F + f2 - f), f, (0.5F + f2), (0.5F + f2));
        this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
        GL11.glPopMatrix();
        this.renderBlocks.unlockBlockBounds();
        this.renderBlocks.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }
	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {
		render((EntityCraftingFrameII)entity, d0, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		return null;
	}

}
