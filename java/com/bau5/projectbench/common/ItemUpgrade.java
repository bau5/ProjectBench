package com.bau5.projectbench.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Rick on 5/18/2015.
 */
public class ItemUpgrade extends Item{

    public static final String[] names = {
            "Fluid"
    };

    public ItemUpgrade(){
        setMaxStackSize(4);
        setHasSubtypes(true);
        setUnlocalizedName("pb_upgrade");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return (super.getUnlocalizedName(stack) + "_" +names[stack.getMetadata()]).toLowerCase();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Upgrade: " + names[stack.getMetadata()];
    }
}
