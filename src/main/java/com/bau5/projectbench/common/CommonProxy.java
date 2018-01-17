package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.ContainerProjectBench;
import com.bau5.projectbench.common.item.ItemPlan;
import com.bau5.projectbench.common.item.ItemUpgrade;
import com.bau5.projectbench.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by bau5 on 4/15/2015.
 */
public class CommonProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerProjectBench(player.inventory, (TileEntityProjectBench)world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void registerRenderingInformation(){}


}
