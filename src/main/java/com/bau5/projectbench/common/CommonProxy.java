package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.ContainerProjectBench;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


/**
 * Created by bau5 on 4/15/2015.
 */
public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerProjectBench(player.inventory, (TileEntityProjectBench) world.getTileEntity(x, y, z));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void registerRenderingInformation() {}
}
