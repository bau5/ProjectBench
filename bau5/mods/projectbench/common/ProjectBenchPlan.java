package bau5.mods.projectbench.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import bau5.mods.projectbench.common.recipes.RecipeCrafter;
import bau5.mods.projectbench.common.tileentity.ContainerProjectBench;

public class ProjectBenchPlan extends Item {
	
	private Icon itemIconUsed;

	public ProjectBenchPlan(int id) {
		super(id);
		setMaxDamage(0);
	}
	
	@Override
	public String getItemDisplayName(ItemStack par1ItemStack) {
		if(par1ItemStack.stackTagCompound != null){
			NBTTagCompound tag = par1ItemStack.stackTagCompound.getCompoundTag("Result");
			if(tag != null){
				ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
				return EnumChatFormatting.ITALIC + stack.getDisplayName() + EnumChatFormatting.RESET +" " +StatCollector.translateToLocal(getUnlocalizedName()+".name.used");
			}
			return StatCollector.translateToLocal(getUnlocalizedName()+".name.broken");
		}else
			return StatCollector.translateToLocal(getUnlocalizedName()+".name.blank");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		if(Keyboard.isKeyDown(42) && stack.stackTagCompound != null){
			NBTTagList tag = stack.stackTagCompound.getTagList("Components");
			if(tag != null){
				ItemStack[] stacks = new ItemStack[tag.tagCount()];
				for(int i = 0; i < tag.tagCount(); i++){
					ItemStack stack2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)tag.tagAt(i));
					stacks[i] = stack2;
				}
				RecipeCrafter crafter = new RecipeCrafter();
				ItemStack[] stacks2 = crafter.consolidateItemStacks(stacks);
				ItemStack[] missingStacks = crafter.getMissingStacks((ContainerProjectBench)player.openContainer, stack);
				
				for(ItemStack stack3 : stacks2){
					boolean flag = false;
					if(player.openContainer instanceof ContainerProjectBench){
						if(missingStacks != null && missingStacks.length > 0){
							for(ItemStack missingStack : missingStacks){
								if(crafter.checkItemMatch(stack3, missingStack, false)){
									flag = true;
									break;
								}
							}
						}
					}
					list.add((flag ? EnumChatFormatting.DARK_RED : EnumChatFormatting.DARK_GREEN) + stack3.getDisplayName() + " x " +stack3.stackSize);
					flag = false;
				}
			}
		}
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("projectbench:planblank");
		itemIconUsed = register.registerIcon("projectbench:planused");
	}
	@Override
	public Icon getIconFromDamage(int meta) {
		if(meta == 1){
			return itemIconUsed;
		}
		return meta == 0 ? itemIcon : itemIconUsed;
	}
}
