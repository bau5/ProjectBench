package com.bau5.projectbench.client;

import com.bau5.projectbench.common.CommonProxy;
import com.bau5.projectbench.common.TileEntityProjectBench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(world.getTileEntity(new BlockPos(x, y, z))instanceof TileEntityProjectBench) {
            return new GuiProjectBench(player.inventory, (TileEntityProjectBench) world.getTileEntity(new BlockPos(x, y, z)));
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerRenderingInformation() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, new ProjectBenchRenderer());
    }
}
