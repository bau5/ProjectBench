package bau5.mods.projectbench.common;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ProjectBenchBlock extends BlockContainer {

	public ProjectBenchBlock(int id, Material mat) 
	{
		super(id, mat);
		setHardness(2.0F);
		setResistance(1.5F);
		setBlockName("bau5ProjectBench");
	}
	
	public String getTextureFile()
	{
		return ProjectBench.textureFile;
	}
	public int getBlockTextureFromSide(int side)
    {
		switch(side)
		{
		case 0: return 2;
		case 1: return 1;
		default: return 0;
		}
	}
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving el)
    {
        byte byt = 0;
        int dir = MathHelper.floor_double((double)(el.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (dir == 0)
        {
            byt = 2;
        }

        if (dir == 1)
        {
            byt = 5;
        }

        if (dir == 2)
        {
            byt = 3;
        }

        if (dir == 3)
        {
            byt = 4;
        }
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if(te != null && te instanceof TileEntityProjectBench)
        {
        	TileEntityProjectBench tpb = (TileEntityProjectBench)te;
        	tpb.setDirectionFacing(byt);
        }
    }
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
			 int par6, float par7, float par8, float par9)
    {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null || player.isSneaking())
		{	
			return false;
		}
        player.openGui(ProjectBench.instance, 0, world, x, y, z);
		return true;
    }
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	private void dropItems(World world, int x, int y, int z) 
	{
		Random rand = new Random();
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(!(te instanceof IInventory))
			return;
		IInventory inv = (IInventory)te;
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack item = inv.getStackInSlot(i);
			if(item != null && item.stackSize > 0)
			{
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;
				EntityItem ei = new EntityItem(world, x + rx, y + ry, z + rz,
						new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
				if(item.hasTagCompound())
					ei.item.setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				float factor = 0.05f;
				ei.motionX = rand.nextGaussian() * factor;
				ei.motionY = rand.nextGaussian() * factor + 0.2F;
				ei.motionZ = rand.nextGaussian() * factor;
				if(!world.isRemote)
					world.spawnEntityInWorld(ei);
				item.stackSize = 0;
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1) 
	{
		return new TileEntityProjectBench();
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}
//	@Override
//	public int getRenderType()
//	{
//		return ProjectBench.instance.pbRenderID;
//	}


}
