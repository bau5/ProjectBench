package com.bau5.projectbench.common;

import com.bau5.projectbench.common.item.ItemPlan;
import com.bau5.projectbench.common.item.ItemUpgrade;
import com.bau5.projectbench.common.utils.Config;
import com.bau5.projectbench.common.utils.Reference;
import com.bau5.projectbench.common.utils.VersionCheckEventHandler;
import com.bau5.projectbench.common.utils.VersionChecker;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
@Mod.EventBusSubscriber
public class ProjectBench {

    @SidedProxy(clientSide = Reference.PROXY_CLIENT,
                serverSide = Reference.PROXY_SERVER)
    public static CommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static ProjectBench instance;
    public static SimpleNetworkWrapper network;

    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":pb_block")
    public static BlockProjectBench projectBench;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":plan")
    public static ItemPlan plan;
    @GameRegistry.ObjectHolder(Reference.MOD_ID + ":pb_upgrade")
    public static ItemUpgrade upgrade;

    public Config config;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent ev){
        config = new Config(ev);
   }

    @Mod.EventHandler
    public void init(FMLInitializationEvent ev) {
        NetworkRegistry.INSTANCE.registerGuiHandler(ProjectBench.instance, proxy);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);
        network.registerMessage(SimpleMessage.Handler.class, SimpleMessage.class, 0, Side.SERVER);
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

        proxy.registerRenderingInformation();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        BlockProjectBench projectBench = new BlockProjectBench();
        projectBench.setCreativeTab(CreativeTabs.DECORATIONS);
        event.getRegistry().register(projectBench);
    }

    // GameRegistry.registerTileEntity(TileEntityProjectBench.class, "pb_te");

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event){
        ItemPlan plan = new ItemPlan();
        ItemUpgrade upgrade = new ItemUpgrade();
        event.getRegistry().registerAll(plan, upgrade);
    }

    /*
    private void registerRecipes(){
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(projectBench, 1, 0),
            " G ", "ICI", "WHW", 'G', "blockGlass", 'I', "ingotIron", 'C', Blocks.crafting_table,
            'W', "plankWood", 'H', Blocks.chest
        ));
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(plan, 8, 0),
            " PS", "PNP", "SP ", 'P', Items.paper, 'S', Items.stick, 'N', Items.gold_nugget
        ));
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(upgrade, 1, 0),
            " G ", "I I", "WHW", 'G', "blockGlass", 'I', "ingotIron",
            'W', "plankWood", 'H', Blocks.chest
        ));
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(upgrade, 1, 1),
            "SGS", "GBG", "SGS", 'S', "stone", 'G', "blockGlass", 'B', Items.bucket
        ));
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(upgrade, 1, 2),
            " I ", "ICI", " I ", 'I', Items.iron_ingot, 'C', Blocks.chest
        ));

        if (Config.DEBUG_RECIPE) {
            CraftingManager.getInstance().addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Items.water_bucket)));
        }
    }
    */
}
