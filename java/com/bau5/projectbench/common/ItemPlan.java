package com.bau5.projectbench.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by Rick on 4/17/2015.
 */
public class ItemPlan extends Item {

    public ItemPlan(){
        setMaxStackSize(16);
        setHasSubtypes(true);
        setUnlocalizedName("plan_");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String str = super.getUnlocalizedName(stack);
        if(stack.hasTagCompound()) {
            return str + "used";
        }
        return str + "blank";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Result")){
            NBTTagCompound tag = (NBTTagCompound)stack.getTagCompound().getTag("Result");
            if(tag != null) {
                ItemStack result = ItemStack.loadItemStackFromNBT(tag);
                if(result != null){
                    return "Plan: " + result.getDisplayName();
                }
            }else{
                return "Broken Plan";
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }
}
