package com.bau5.projectbench.common.item;

import com.bau5.projectbench.common.utils.PlanHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bau5 on 4/17/2015.
 */
public class ItemPlan extends Item {

    private IIcon[] icons;

    public ItemPlan() {
        setMaxStackSize(16);
        setHasSubtypes(true);
        setUnlocalizedName("plan");
    }

    @Override
    public void registerIcons(IIconRegister registrar) {
        icons = new IIcon[2];
        icons[0] = registrar.registerIcon("projectbench:plan_blank");
        icons[1] = registrar.registerIcon("projectbench:plan_used");
    }


    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return icons[p_77617_1_];
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return icons[stack.getItemDamage()];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String str = super.getUnlocalizedName(stack);
        if (stack.hasTagCompound()) {
            return str + "_used";
        }
        return str + "_blank";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Result")) {
            NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().getTag("Result");
            if (tag != null) {
                ItemStack result = ItemStack.loadItemStackFromNBT(tag);
                if (result != null) {
                    return EnumChatFormatting.BLUE + "Plan: " + EnumChatFormatting.WHITE + result.getDisplayName();
                }
            } else {
                return "Broken Plan";
            }
        }
        return "Blank Plan";
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            ItemStack[] stacks = PlanHelper.getComponentsForPlan(stack);
            if (stacks != null) {
                ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
                for (ItemStack part : stacks) {
                    if (part == null) {
                        continue;
                    }
                    boolean flag = false;
                    for (ItemStack have : stackList) {
                        if (have.getItem().equals(part.getItem()) && have.getItemDamage() == part.getItemDamage() && have.getTagCompound() == part.getTagCompound()) {
                            have.stackSize += stack.stackSize;
                            flag = true;
                            break;
                        }
                    }
                    if (! flag) {
                        stackList.add(part);
                    }
                }
                for (ItemStack part : stackList) {
                    if (part == null) {
                        continue;
                    }
                    String disp = "" + EnumChatFormatting.GRAY + part.stackSize + " x " + part.getDisplayName();
                    tooltip.add(disp);
                }
            }
        }
        super.addInformation(stack, playerIn, tooltip, advanced);

    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }
}
