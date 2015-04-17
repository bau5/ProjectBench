package com.bau5.projectbench.common;

import com.bau5.projectbench.client.TileEntityProjectBench;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = ProjectBench.MOD_ID, name = ProjectBench.NAME, version = ProjectBench.VERSION)
public class ProjectBench {
    public final static String MOD_ID = "projectbench";
    public final static String VERSION = "0.1";
    public final static String NAME = "Project Bench";

    @SidedProxy(clientSide = "com.bau5.projectbench.client.ClientProxy",
                serverSide = "com.bau5.projectbench.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ProjectBench.MOD_ID)
    public static ProjectBench instance;

    public static Block projectBench;
    public static boolean tryOreDictionary = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        projectBench = new BlockProjectBench().setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(projectBench, ItemBlockProjectBench.class, "pb_block");
        GameRegistry.registerTileEntity(TileEntityProjectBench.class, "pb_te");
        NetworkRegistry.INSTANCE.registerGuiHandler(ProjectBench.instance, proxy);
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(projectBench, 1, 0), new Object[] {
                " G ", "ICI", "WHW", 'G', "blockGlass", 'I', "ingotIron", 'C', Blocks.crafting_table,
                                     'W', "plankWood",  'H', Blocks.chest
        }));
        proxy.registerRenderingInformation();
    }
}
