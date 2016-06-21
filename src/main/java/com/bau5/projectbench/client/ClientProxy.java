package com.bau5.projectbench.client;

import com.bau5.projectbench.common.CommonProxy;
import com.bau5.projectbench.common.ProjectBench;
import com.bau5.projectbench.common.TileEntityProjectBench;
import com.bau5.projectbench.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
        String inv = "inventory";
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                Item.getItemFromBlock(ProjectBench.projectBench), 0,
                new ModelResourceLocation(Reference.MOD_ID + ":pb_block", inv));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.plan, 1,
                new ModelResourceLocation(Reference.MOD_ID + ":planused", inv));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.plan, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":plan", inv));

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.upgrade, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_pb", inv));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.upgrade, 1,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_fluid", inv));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                ProjectBench.upgrade, 2,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_fluid", inv));

        ModelBakery.registerItemVariants(ProjectBench.plan, new ResourceLocation("projectbench:plan"), new ResourceLocation("projectbench:planused"));
        ModelBakery.registerItemVariants(ProjectBench.upgrade, new ResourceLocation("projectbench:upgrade_pb"), new ResourceLocation("projectbench:upgrade_fluid"), new ResourceLocation("projectbench:upgrade_inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, new ProjectBenchRenderer());
    }
}
