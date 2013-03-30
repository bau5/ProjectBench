package bau5.mods.projectbench.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class PBItemBlock extends ItemBlock{

	private final static String blockNames[] = {
		"Project BenchI", "Project BenchII", "Project BenchIII"
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
	public String getItemDisplayName(ItemStack stack) {
		return new StringBuilder().append("block").append(blockNames[stack.getItemDamage()]).toString();
	}
}
