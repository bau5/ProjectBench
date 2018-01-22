package com.bau5.projectbench.common.utils;

import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.TileEntityProjectBench;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.asm.transformers.ItemStackTransformer;

/**
 * Created by bau5 on 5/15/2015.
 */
public class PlanHelper {

    public static final String result = "Result";
    public static final String title = "Plan";

    public static ItemStack getPlanResult(ItemStack plan){
        if(plan == null || !plan.hasTagCompound() || !plan.getTagCompound().hasKey(PlanHelper.result)){
            return ItemStack.EMPTY;
        }
        return new ItemStack(plan.getTagCompound().getCompoundTag(PlanHelper.result));
    }

    public static NonNullList<ItemStack> getComponentsForPlan(ItemStack plan){
        // TODO: use ItemStackHelper.loadAllItems();
        if(plan == null || !plan.hasTagCompound())
            return NonNullList.create();
        NBTTagList list = plan.getTagCompound().getTagList(PlanHelper.title, 10);
        if(list.tagCount() == 0){
            return NonNullList.create();
        }

        NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
        for(int i = 0; i < list.tagCount(); i++){
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int index = tag.getByte("Slot") & 255;
            if(index < 9) {
                stacks.set(index,new ItemStack(tag));
            }
        }
        return stacks;
    }

    public static void writePlan(TileEntityProjectBench tile) {
        ItemStack planStack = tile.getPlan();
        if(tile.getResult() == ItemStack.EMPTY)
            return;
        if(planStack != ItemStack.EMPTY && planStack.getItem().equals(ProjectBench.plan) && !planStack.hasTagCompound()){
            NBTTagCompound stackTag = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for(int i = 0; i < 9; i++){
                ItemStack component = tile.getStackInSlot(i);
                if(component != ItemStack.EMPTY){
                    component = component.copy();
                    component.setCount(1);
                    if(component.getMaxDamage() > 0 && component.getItemDamage() != 0){
                        component.setItemDamage(0);
                    }
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setByte("Slot", (byte)i);
                    component.writeToNBT(tag);
                    list.appendTag(tag);
                }
            }
            stackTag.setTag(PlanHelper.result, tile.getResult().writeToNBT(new NBTTagCompound()));
            stackTag.setTag(PlanHelper.title, list);
            planStack.setTagCompound(stackTag);
            planStack.setItemDamage(1);
        }
    }

}
