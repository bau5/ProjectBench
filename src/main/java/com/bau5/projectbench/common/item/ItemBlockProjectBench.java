package com.bau5.projectbench.common.item;

import com.bau5.projectbench.common.ProjectBench;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

/**
 * Created by bau5 on 4/15/2015.
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(ProjectBench.projectBench, 1, 0));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.getMetadata() > names.length-1)
            return "malformed";
        return names[stack.getMetadata()];
    }
}
