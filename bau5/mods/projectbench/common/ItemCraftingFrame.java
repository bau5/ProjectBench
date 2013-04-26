package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemCraftingFrame extends ItemHangingEntity {
	
	private Class hanging = null; 
	public ItemCraftingFrame(int id, Class clas) 
	{
		super(id, clas);
		hanging = clas;
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 == 1)
        {
            return false;
        }
        else
        {
            int i1 = Direction.facingToDirection[par7];
            EntityHanging entityhanging = this.createHangingEntity(world, par4, par5, par6, i1);
            if (!player.canPlayerEdit(par4, par5, par6, par7, stack))
            {
                return false;
            }
            else
            {
                if (entityhanging != null && entityhanging.onValidSurface())
                {
                    if (!world.isRemote)
                    {
                        world.spawnEntityInWorld(entityhanging);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }
    }

    /**
     * Create the hanging entity associated to this item.
     */
    private EntityHanging createHangingEntity(World par1World, int par2, int par3, int par4, int par5)
    {
    	if(hanging == EntityCraftingFrame.class)
    		return new EntityCraftingFrame(par1World, par2, par3, par4, par5);
    	if(hanging == EntityCraftingFrameII.class)
    		return new EntityCraftingFrameII(par1World, par2, par3, par4, par5);
    	return null;
    }
    
    @Override
    public void registerIcons(IconRegister ir) {
		if(hanging == EntityCraftingFrame.class)
			itemIcon = ir.registerIcon("projectbench:craftingframe");
		else if(hanging == EntityCraftingFrameII.class)
			itemIcon = ir.registerIcon("projectbench:craftingframeii");
    }
}
