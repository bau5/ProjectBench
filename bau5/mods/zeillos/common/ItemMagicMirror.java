package bau5.mods.zeillos.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMagicMirror extends Item{

	public ItemMagicMirror(int id) {
		super(id);
		setMaxDamage(0);
		setMaxStackSize(1);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world,
			EntityPlayer player, int duration) {
		if(duration == getMaxItemUseDuration(itemStack)){
			System.out.println("yay");
		}
		super.onPlayerStoppedUsing(itemStack, world, player, duration);
	}
	@Override
	public void registerIcons(IconRegister reg) {
//		itemIcon = reg.registerIcon(Reference.TEXTURE_NAME+"magicmirror");
		itemIcon = appleGold.getIconFromDamage(0);
	}
	
	@Override
	public ItemStack onEaten(ItemStack itemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Auto-generated method stub
		return itemStack;
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "Magic Mirror";
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 50;
	}
	@Override
	public boolean onItemUse(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, World par3World, int par4, int par5,
			int par6, int par7, float par8, float par9, float par10) {
		// TODO Auto-generated method stub
		return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5,
				par6, par7, par8, par9, par10);
	}
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
		return itemStack;
	}
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
//        return EnumAction.bow;
		return super.getItemUseAction(par1ItemStack);
    }
}
