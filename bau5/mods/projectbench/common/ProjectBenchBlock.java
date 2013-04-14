package bau5.mods.projectbench.common;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProjectBenchBlock extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	
	public ProjectBenchBlock(int id, Material mat) 
	{
		super(id, mat);
		setHardness(2.0F);
		setResistance(1.5F);
		setUnlocalizedName("bau5ProjectBench");
	}
	@Override
	public Icon getBlockTextureFromSideAndMetadata(int i, int j){
		switch(j){
		case 0:
			switch(i){
			case 0: return icons[2];
			case 1: return icons[1];
			default: return icons[0];
			}
		case 1:
			switch(i){
			case 0: return icons[5];
			case 1: return icons[4];
			default: return icons[3];
			}
		default: return icons[0];
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IconRegister register)
    {
		icons = new Icon[6];
        icons[0] = register.registerIcon("projectbench:pbblock0");
        icons[1] = register.registerIcon("projectbench:pbblock1");
        icons[2] = register.registerIcon("projectbench:pbblock2");
        icons[3] = register.registerIcon("projectbench:pbblockii0");
        icons[4] = register.registerIcon("projectbench:pbblockii1");
        icons[5] = register.registerIcon("projectbench:pbblockii2");
    }
	
	@Override
	public void onBlockAdded(World world, int i, int j, int k)
	{
		super.onBlockAdded(world, i, j, k);
		world.markBlockForUpdate(i, j, k);
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
		int meta = world.getBlockMetadata(x, y, z);

		switch(meta){
			case 0:
				player.openGui(ProjectBench.instance, 0, world, x, y, z);
				break;
			case 1:
				player.openGui(ProjectBench.instance, 1, world, x, y, z);
				break;
		}
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
		int i = 0; 
		int size = inv.getSizeInventory();
		if(te instanceof TEProjectBenchII)
			i = 27;
		for(; i < size; i++)
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
					ei.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving el, ItemStack stack)
    {
        byte dir = 0;
        int plyrFacing = MathHelper.floor_double((double) ((el.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        if (plyrFacing == 0)
            dir = 2;
        if (plyrFacing == 1)
            dir = 5;
        if (plyrFacing == 2)
            dir = 3;
        if (plyrFacing == 3)
            dir = 4;
        TileEntity tpb = world.getBlockTileEntity(x, y, z);
        if (tpb != null && tpb instanceof TEProjectBenchII)
        {
            ((TEProjectBenchII)tpb).setDirection(dir);
            world.markBlockForUpdate(x, y, z);
        }
    }
	public TileEntity createTileEntity(World world, int metadata)
    {
		switch(metadata){
		case 0: return new TileEntityProjectBench();
		case 1: if(ProjectBench.DEV_ENV) return new TEProjectBenchII();
		default: return null;
		}
    }
	@Override
	public TileEntity createNewTileEntity(World var1) 
	{
		return null;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        if(ProjectBench.DEV_ENV)
        	par3List.add(new ItemStack(par1, 1, 1));
    }
	
	@Override
	public int damageDropped(int meta) {
		// TODO Auto-generated method stub
		return meta;
	}
}
