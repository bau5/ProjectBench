package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * PBItemBlock
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class PBItemBlock extends ItemBlock{

	private final static String blockNames[] = {
		"Mk. I", "Mk. II", "Mk. III"
	};
	public PBItemBlock(int id) {
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public int getMetadata(int meta){
		return meta;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		par3List.add(blockNames[par1ItemStack.getItemDamage()]);
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack) {
		return "Project Bench";
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return this.getUnlocalizedName() + blockNames[par1ItemStack.getItemDamage()];
	}
}
