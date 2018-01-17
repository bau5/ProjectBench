package com.bau5.projectbench.common.item;

import com.bau5.projectbench.common.ProjectBench;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by bau5 on 5/18/2015.
 */
@SuppressWarnings("unchecked")
public class ItemUpgrade extends Item{

    public static final String[] names = {
            "Project Bench", "Fluid", "Inventory Size"
    };

    public ItemUpgrade(){
        setMaxStackSize(4);
        setHasSubtypes(true);
        setUnlocalizedName("pb_upgrade");
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack heldStack = player.getHeldItem(hand);
        // TODO: Using meta data is confusing here (and everywhere)
        // heldStack.getMetadata() == Upgrade.ProjectBench
        if(heldStack.getMetadata() == 0 && world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE){
            if(!world.isRemote) {
                IBlockState newState = ProjectBench.projectBench.getDefaultState();
                world.setBlockState(pos, newState, 1 | 2);
                newState.getBlock().onBlockAdded(world, pos, newState);

                if(!player.capabilities.isCreativeMode) {
                    player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() - 1);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return (super.getUnlocalizedName(stack) + "_" +names[stack.getMetadata()]).toLowerCase();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        String more = "" + ChatFormatting.GRAY;
        switch(stack.getMetadata()){
            case 0: more += "Used to upgrade crafting table to Project Bench.";
                break;
            case 1: more += "Used to upgrade Project Bench to handle fluids.";
                break;
            case 2: more += "Used to upgrade Project Bench with more inventory slots.";
                break;
        }
        tooltip.add(more);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Upgrade: " + names[stack.getMetadata()];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 1));
        subItems.add(new ItemStack(this, 1, 2));
    }
}
