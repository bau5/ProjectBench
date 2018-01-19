package com.bau5.projectbench.common.item;

import com.bau5.projectbench.common.utils.PlanHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bau5 on 4/17/2015.
 */
public class ItemPlan extends Item {

    public ItemPlan(){
        setMaxStackSize(16);
        setHasSubtypes(true);
        setUnlocalizedName("plan");
        setRegistryName("plan");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String str = super.getUnlocalizedName(stack);
        if(stack.hasTagCompound()) {
            return str + "_used";
        }
        return str + "_blank";
    }

    // TODO: is there a better way to get this display name?
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(PlanHelper.result)){
            NBTBase base = stack.getTagCompound().getTag(PlanHelper.result);
            if (base instanceof NBTTagCompound) {
                ItemStack result = new ItemStack((NBTTagCompound)base);
                return ChatFormatting.BLUE + "Plan: " + ChatFormatting.WHITE +result.getDisplayName();
            }
        }
        return "Blank Plan";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ){
            ItemStack[] stacks = PlanHelper.getComponentsForPlan(stack);
            if(stacks != null) {
                ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
                for (ItemStack part : stacks) {
                    if (part == null)
                        continue;
                    boolean flag = false;
                    for (ItemStack have : stackList) {
                        if (have.getItem().equals(part.getItem()) && have.getMetadata() == part.getMetadata() && have.getTagCompound() == part.getTagCompound()) {
                            have.setCount(have.getCount() + stack.getCount());
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                        stackList.add(part);
                }
                for (ItemStack part : stackList) {
                    if (part == null)
                        continue;
                    tooltip.add(String.format("%s %d x %s", ChatFormatting.GRAY, part.getCount(), part.getDisplayName()));
                }
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        //items.add(new ItemStack(ProjectBench.plan, 1, 0));
        super.getSubItems(tab, items);
    }
}
