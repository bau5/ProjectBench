package com.bau5.projectbench.client;

import com.bau5.projectbench.common.CommonProxy;
import com.bau5.projectbench.common.TileEntityProjectBench;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by bau5 on 4/15/2015.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(world.getTileEntity(x, y, z) instanceof TileEntityProjectBench)
            return new GuiProjectBench(player.inventory, (TileEntityProjectBench)world.getTileEntity(x, y, z));
        else
            return null;
    }

    @Override
    public void registerRenderingInformation() {
//        String inv = "inventory";
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
//                Item.getItemFromBlock(ProjectBench.projectBench), 0,
//                new ModelResourceLocation(Reference.MOD_ID + ":pb_block", inv));
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
//                ProjectBench.plan, 1,
//                new ModelResourceLocation(Reference.MOD_ID + ":planused", inv));
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
//                ProjectBench.plan, 0,
//                new ModelResourceLocation(Reference.MOD_ID + ":plan", inv));
//
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
//                ProjectBench.upgrade, 0,
//                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_pb", inv));
//        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
//                ProjectBench.upgrade, 1,
//                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_fluid", inv));
//
//        ModelBakery.addVariantName(ProjectBench.plan, "projectbench:plan", "projectbench:planused");
//        ModelBakery.addVariantName(ProjectBench.upgrade, "projectbench:upgrade_pb", "projectbench:upgrade_fluid");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, new ProjectBenchRenderer());
    }
}
