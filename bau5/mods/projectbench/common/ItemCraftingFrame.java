package bau5.mods.projectbench.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemCraftingFrame extends ItemHangingEntity {
	
	public ItemCraftingFrame(int id, Class clas) 
	{
		super(id, clas);
		setUnlocalizedName("craftingframe");
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 == 0)
        {
            return false;
        }
        else if (par7 == 1)
        {
            return false;
        }
        else
        {
            int i1 = Direction.vineGrowth[par7];
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
        return (EntityHanging)new EntityCraftingFrame(par1World, par2, par3, par4, par5);
    }

    @Override
    public void updateIcons(IconRegister ir) {
    	iconIndex = ir.registerIcon("projectbench:craftingframe");
    }
    
}
