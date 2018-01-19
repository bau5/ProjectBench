package com.bau5.projectbench.common;

import com.bau5.projectbench.common.utils.Reference;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


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
}
