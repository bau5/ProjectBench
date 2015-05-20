package com.bau5.projectbench.client;

import com.bau5.projectbench.common.CommonProxy;
import com.bau5.projectbench.common.ProjectBench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(world.getTileEntity(new BlockPos(x, y, z))instanceof TileEntityProjectBench)
            return new GuiProjectBench(player.inventory, (TileEntityProjectBench)world.getTileEntity(new BlockPos(x, y, z)));
        else
            return null;
    }

    @Override
    public void registerRenderingInformation() {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                Item.getItemFromBlock(ProjectBench.projectBench), 0,
                new ModelResourceLocation(ProjectBench.MOD_ID + ":pb_block", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.plan, 1,
                new ModelResourceLocation(ProjectBench.MOD_ID + ":planused", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.plan, 0,
                new ModelResourceLocation(ProjectBench.MOD_ID + ":plan", "inventory"));

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.upgrade, 0,
                new ModelResourceLocation(ProjectBench.MOD_ID + ":upgrade_pb", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.upgrade, 1,
                new ModelResourceLocation(ProjectBench.MOD_ID + ":upgrade_fluid", "inventory"));

        ModelBakery.addVariantName(ProjectBench.plan, "projectbench:plan", "projectbench:planused");
        ModelBakery.addVariantName(ProjectBench.upgrade, "projectbench:upgrade_pb", "projectbench:upgrade_fluid");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, new ProjectBenchRenderer());
    }
}
