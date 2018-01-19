package com.bau5.projectbench.common;

import com.bau5.projectbench.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
@SuppressWarnings("unused")
public class Registrar {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ProjectBench.projectBench.setCreativeTab(CreativeTabs.DECORATIONS);
        event.getRegistry().register(ProjectBench.projectBench);
        GameRegistry.registerTileEntity(TileEntityProjectBench.class, "pb_te");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().registerAll(
                new ItemBlock(ProjectBench.projectBench).setRegistryName(ProjectBench.projectBench.getRegistryName())
        );
        event.getRegistry().registerAll(ProjectBench.plan, ProjectBench.upgrade);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(final ModelRegistryEvent event) {
        String inv = "inventory";
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ProjectBench.projectBench), 0,
                new ModelResourceLocation(Reference.MOD_ID + ":pb_block", inv));
        ModelLoader.setCustomModelResourceLocation(
                ProjectBench.plan, 1,
                new ModelResourceLocation(Reference.MOD_ID + ":planused", inv));
        ModelLoader.setCustomModelResourceLocation(
                ProjectBench.plan, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":plan", inv));

        ModelLoader.setCustomModelResourceLocation(
                ProjectBench.upgrade, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_pb", inv));
        ModelLoader.setCustomModelResourceLocation(
                ProjectBench.upgrade, 1,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_fluid", inv));
        ModelLoader.setCustomModelResourceLocation(
                ProjectBench.upgrade, 2,
                new ModelResourceLocation(Reference.MOD_ID + ":upgrade_inventory", inv));
    }
}
