package com.bau5.projectbench.common;

import com.bau5.projectbench.common.item.ItemBlockProjectBench;
import com.bau5.projectbench.common.item.ItemPlan;
import com.bau5.projectbench.common.item.ItemUpgrade;
import com.bau5.projectbench.common.utils.Config;
import com.bau5.projectbench.common.utils.Reference;
import com.bau5.projectbench.common.utils.VersionCheckEventHandler;
import com.bau5.projectbench.common.utils.VersionChecker;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class ProjectBench{

    @SidedProxy(clientSide = Reference.PROXY_CLIENT,
                serverSide = Reference.PROXY_SERVER)
    public static CommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static ProjectBench instance;
    public static SimpleNetworkWrapper network;

    public static Block projectBench;
    public static boolean tryOreDictionary = true;
    public static Item plan;
    public static Item upgrade;

    public Config config;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent ev){
        config = new Config(ev);
        registerItemsAndBlocks(ev);
        registerRecipes();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent ev) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ProjectBench.instance, proxy);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);
        network.registerMessage(SimpleMessage.Handler.class, SimpleMessage.class, 0, Side.SERVER);
        proxy.registerRenderingInformation();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent ev){
        if(ev.getSide() == Side.CLIENT){
            if(Config.VERSION_CHECK) {
                VersionChecker.go();
                MinecraftForge.EVENT_BUS.register(new VersionCheckEventHandler());
            }else{
                FMLLog.info("[Project Bench] Version checking disabled.");
            }
        }
        FMLLog.info("[Project Bench] Initialization complete. Fluids found:");
        for(String name : FluidRegistry.getRegisteredFluids().keySet()){
            FMLLog.info("\t%s", name);
        }
        FMLLog.info("These fluids and their containers will be recognized by the Project Bench.");
    }

    private void registerItemsAndBlocks(FMLPreInitializationEvent ev){
        projectBench = new BlockProjectBench().setCreativeTab(CreativeTabs.tabDecorations);
        plan = new ItemPlan().setCreativeTab(CreativeTabs.tabMisc);
        upgrade = new ItemUpgrade().setCreativeTab(CreativeTabs.tabMisc);
        GameRegistry.registerBlock(projectBench, ItemBlockProjectBench.class, "pb_block");
        GameRegistry.registerTileEntity(TileEntityProjectBench.class, "pb_te");
        GameRegistry.registerItem(plan, "plan");
        GameRegistry.registerItem(upgrade, "pb_upgrade");
    }

    private void registerRecipes(){
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(projectBench, 1, 0), new Object[]{
                " G ", "ICI", "WHW", 'G', "blockGlass", 'I', "ingotIron", 'C', Blocks.crafting_table,
                'W', "plankWood", 'H', Blocks.chest
        }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plan, 8, 0), new Object[]{
                " PS", "PNP", "SP ", 'P', Items.paper, 'S', Items.stick, 'N', Items.gold_nugget
        }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgrade, 1, 0), new Object[]{
                " G ", "I I", "WHW", 'G', "blockGlass", 'I', "ingotIron",
                'W', "plankWood", 'H', Blocks.chest
        }));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgrade, 1, 1), new Object[]{
                "SGS", "GBG", "SGS", 'S', "stone", 'G', "blockGlass", 'B', Items.bucket
        }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.mossy_cobblestone, 1, 0),
                new ItemStack(Items.water_bucket, 1, 0), new ItemStack(Blocks.cobblestone)
        ));
    }
}
