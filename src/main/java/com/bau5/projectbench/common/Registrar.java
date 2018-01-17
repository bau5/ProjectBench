package com.bau5.projectbench.common;

import com.bau5.projectbench.common.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
@SuppressWarnings("unused")
public class Registrar {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        System.out.println("Register blocks.");
        ProjectBench.projectBench.setCreativeTab(CreativeTabs.DECORATIONS);
        event.getRegistry().register(ProjectBench.projectBench);
    }

    // GameRegistry.registerTileEntity(TileEntityProjectBench.class, "pb_te");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        System.out.println("Register items.");
        event.getRegistry().registerAll(ProjectBench.plan, ProjectBench.upgrade);
    }
}
