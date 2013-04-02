package bau5.mods.projectbench.common;

import java.util.logging.Level;

import bau5.mods.projectbench.common.recipes.RecipeManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

//1.5
@Mod (modid = "bau5_ProjectBench", name = "Project Bench", version = "1.7")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
			channels = {"bau5_PB"}, packetHandler = PBPacketHandler.class)
public class ProjectBench 
{
	@Instance("bau5_ProjectBench")
	public static ProjectBench instance;
	@SidedProxy(clientSide = "bau5.mods.projectbench.client.ClientProxy",
				serverSide = "bau5.mods.projectbench.common.CommonProxy")
	public static CommonProxy proxy;
	
	private static int pbID;
	private static int pbUpID;
	public static boolean DO_RENDER = true;
	public static boolean RENDER_ALL = false;
	public static boolean II_DO_RENDER = true;
	public static int  SPEED_FACTOR = 5;
	
	public Block projectBench;
	public Item  projectBenchUpgrade;
	public static String baseTexFile = "/mods/projectbench/textures";
	public static String textureFile = baseTexFile + "/pbsheet.png";
  
	@PreInit
	public void preInit(FMLPreInitializationEvent ev)
	{
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		try
		{
			config.load(); 
			pbID = config.getBlock("Project Bench", 700).getInt(700);
			pbUpID = config.getItem(Configuration.CATEGORY_ITEM, "Upgrade Item", 13070).getInt(13070);
			DO_RENDER = config.get(Configuration.CATEGORY_GENERAL, "shouldRenderItem", true).getBoolean(true);
			II_DO_RENDER = config.get(Configuration.CATEGORY_GENERAL, "shouldIIRenderItems", true).getBoolean(true);
			RENDER_ALL = config.get(Configuration.CATEGORY_GENERAL, "shouldRenerStackSize", false).getBoolean(false);
			SPEED_FACTOR = config.get(Configuration.CATEGORY_GENERAL, "speedFactor", 5).getInt(5);
			if(SPEED_FACTOR < 0)
			{
				SPEED_FACTOR = 5;
				FMLLog.severe("Project Bench: Config registered a negative number.\n\t Using default of " +SPEED_FACTOR);
			}
		} catch(Exception ex)
		{
			FMLLog.log(Level.SEVERE, ex, "Project Bench: Error encountered while loading config file.");
		} finally 
		{ 
			config.save(); 
		}
	}
	
	@Init
	public void initMain(FMLInitializationEvent ev)
	{
		projectBench = new ProjectBenchBlock(pbID, Material.wood).setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerBlock(projectBench, PBItemBlock.class, "pb_block");
		proxy.registerRenderInformation();
		projectBenchUpgrade = new PBUpgradeItem(pbUpID).setCreativeTab(CreativeTabs.tabMisc);
		System.out.println("ProjectBench: Registered block id @ " +pbID +". Rendering: " +DO_RENDER +" @: " +SPEED_FACTOR);
		GameRegistry.registerTileEntity(TileEntityProjectBench.class, "bau5pbTileEntity");
		GameRegistry.registerTileEntity(TEProjectBenchII.class, "bau5pbTileEntityII");

		LanguageRegistry.addName(projectBenchUpgrade, "Project Bench Upgrade");
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		GameRegistry.addRecipe(new ItemStack(this.projectBench, 1, 0), new Object[]{
			" G ", "ICI", "WHW", 'G', Block.glass, 'I', Item.ingotIron, 'C', Block.workbench, 'W', Block.planks, 'H', Block.chest
		});
		GameRegistry.addRecipe(new ItemStack(this.projectBench, 1, 1), new Object[]{
			"IPI", "WDW", "IBI", 'P', projectBench, 'I', Item.ingotIron, 'B', Block.blockSteel, 'D', Item.diamond, 'W', Block.planks
		});
		GameRegistry.addRecipe(new ItemStack(this.projectBenchUpgrade, 1), new Object[]{
			" G ", "IWI", "WHW", 'G', Block.glass, 'I', Item.ingotIron, 'W', Block.planks, 'H', Block.chest
		});
	}
	@PostInit
	public void postInit(FMLPostInitializationEvent ev){
		new RecipeManager();
	}
}
