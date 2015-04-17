package com.bau5.projectbench.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Rick on 4/15/2015.
 */
public class ItemBlockProjectBench extends ItemBlock {

    public String[] names = {
            "Project Bench", "Other Bench"
    };

    public ItemBlockProjectBench(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.getMetadata() > names.length-1)
            return "malformed";
        return names[stack.getMetadata()];
    }
}
