package com.bau5.projectbench.common.item;

import com.bau5.projectbench.common.ProjectBench;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;


/**
 * Created by bau5 on 5/18/2015.
 */
public class ItemUpgrade extends Item {

    public IIcon[] icons;

    public static final String[] names = {
            "Project Bench", "Fluid"
    };

    public ItemUpgrade() {
        setMaxStackSize(4);
        setHasSubtypes(true);
        setUnlocalizedName("pb_upgrade");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block blck = world.getBlock(x, y, z);
        if (stack.getItemDamage() == 0 && blck != null && blck == Blocks.crafting_table) {
            if (! world.isRemote) {
                Block projectBench = ProjectBench.projectBench;
                world.setBlock(x, y, z, projectBench, 0, 3);
                projectBench.onBlockAdded(world, x, y, z);
                if (! player.capabilities.isCreativeMode) {
                    player.getHeldItem().stackSize--;
                }
                return true;
            }
        }
        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public void registerIcons(IIconRegister registrar) {
        icons = new IIcon[2];
        icons[0] = registrar.registerIcon("projectbench:upgrade_pb");
        icons[1] = registrar.registerIcon("projectbench:upgrade_fluid");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return icons[stack.getItemDamage()];
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return icons[p_77617_1_];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return (super.getUnlocalizedName(stack) + "_" + names[stack.getItemDamage()]).toLowerCase();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        String more = "" + EnumChatFormatting.GRAY;
        switch (stack.getItemDamage()) {
            case 0:
                more += "Used to upgrade crafting table to Project Bench.";
                break;
            case 1:
                more += "Used to upgrade Project Bench to handle fluids.";
                break;
        }
        tooltip.add(more);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Upgrade: " + names[stack.getItemDamage()];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 1));
    }
}
