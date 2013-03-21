package bau5.mods.projectbench.common;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
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
	
	public String getTextureFile()
	{
		return ProjectBench.textureFile;
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int i, int j){
		switch(i){
		case 0: return icons[2];
		case 1: return icons[1];
		default: return icons[0];
		}
	}
	
	@SideOnly(Side.CLIENT)
    public void func_94332_a(IconRegister register)
    {
		icons = new Icon[3];
        icons[0] = register.func_94245_a("projectbench:pbblock0");
        icons[1] = register.func_94245_a("projectbench:pbblock1");
        icons[2] = register.func_94245_a("projectbench:pbblock2");
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
	public TileEntity createNewTileEntity(World var1) 
	{
		return new TileEntityProjectBench();
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}
}
