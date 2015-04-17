package com.bau5.projectbench.client;

import com.bau5.projectbench.common.CommonProxy;
import com.bau5.projectbench.common.ProjectBench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
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
public class ClientProxy extends CommonProxy{
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GuiProjectBench(player.inventory, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public void registerRenderingInformation() {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        mesher.register(
                Item.getItemFromBlock(ProjectBench.projectBench), 0,
                new ModelResourceLocation(ProjectBench.MOD_ID +":pb_block", "inventory"));
        mesher.register(ProjectBench.plan, 0, new ModelResourceLocation(ProjectBench.MOD_ID +":plan_", "inventory"));
        String str = ProjectBench.MOD_ID +":plan_";
        ModelBakery.addVariantName(ProjectBench.plan, str +"blank", str +"used");
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjectBench.class, new ProjectBenchRenderer());
    }
}
