package bau5.mods.projectbench.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import bau5.mods.projectbench.common.packets.PBPacketHandler;
import bau5.mods.projectbench.common.recipes.RecipeManager;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
	
/**
 * ProjectBench
 * 
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */
// NEW WORK
//1.5.2 - Forge 656
//Development Environment
@Mod (modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.DEV_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
			channels = {"bau5_PB"}, packetHandler = PBPacketHandler.class)
public class ProjectBench 
{
	@Instance(Reference.MOD_ID)
	public static ProjectBench instance;
	@SidedProxy(clientSide = "bau5.mods.projectbench.client.ClientProxy",
				serverSide = "bau5.mods.projectbench.common.CommonProxy")
	public static CommonProxy proxy;
	
	/**
	 * Index 0 is {@link PBUpgradeItem}. 
	 * Index 1 is {@link ItemCraftingFrame} for Mk I. 
	 * Index 2 is {@link ItemCraftingFrame} for Mk II. 
	 */
	private static int[] pbItemsID = new int[4];
	private static int pbID;
	public static boolean DO_RENDER = true;
	public static boolean RENDER_ALL = false;
	public static boolean II_DO_RENDER = true;
	public static boolean DEBUG_MODE_ENABLED = false;
	public static int  SPEED_FACTOR = 5;
	public static boolean VERSION_CHECK = true;
	
	private int[] entityID = new int[2];
	
	//FIXME dev version!
	public static boolean DEV_ENV = true;

	public Block projectBench;
	public Item  projectBenchUpgrade;
	public Item  projectBenchUpgradeII;
	public Item  craftingFrame;
	public Item  craftingFrameII;
	public static String baseTexFile = "/mods/projectbench/textures";
	public static String textureFile = baseTexFile + "/pbsheet.png";
  
	@PreInit
	public void preInit(FMLPreInitializationEvent ev)
	{
		MinecraftForge.EVENT_BUS.register(new OreRegistrationHandler());
		Configuration config = new Configuration(ev.getSuggestedConfigurationFile());
		try
		{
			config.load(); 
			pbID = config.getBlock("Project Bench", 700).getInt(700);
			pbItemsID[0] = config.getItem(Configuration.CATEGORY_ITEM, "Mk. I Upgrade Item", 13070).getInt(13070);
			pbItemsID[1] = config.getItem(Configuration.CATEGORY_ITEM, "Mk. II Upgrade Item", 13071).getInt(13071);
			pbItemsID[2] = config.getItem(Configuration.CATEGORY_ITEM, "Crafting Frame I", 13072).getInt(13072);
			pbItemsID[3] = config.getItem(Configuration.CATEGORY_ITEM, "Auto-Crafting Frame", 13073).getInt(13073);
			DO_RENDER = config.get(Configuration.CATEGORY_GENERAL, "shouldRenderItem", true).getBoolean(true);
			II_DO_RENDER = config.get(Configuration.CATEGORY_GENERAL, "shouldIIRenderItems", true).getBoolean(true);
			RENDER_ALL = config.get(Configuration.CATEGORY_GENERAL, "shouldRenerStackSize", false).getBoolean(false);
			VERSION_CHECK = config.get(Configuration.CATEGORY_GENERAL, "versionCheckEnabled", true).getBoolean(true);
			SPEED_FACTOR = config.get(Configuration.CATEGORY_GENERAL, "speedFactor", 5).getInt(5);
			if(!DEBUG_MODE_ENABLED)
				DEBUG_MODE_ENABLED = config.get(Configuration.CATEGORY_GENERAL, "debugMode", false).getBoolean(false);
				
			if(SPEED_FACTOR < 0)
			{
				SPEED_FACTOR = 5;
				FMLLog.severe("Project Bench: Config registered a negative number.\n\t Using default of " +SPEED_FACTOR);
			}
			for(int i = 0; i < pbItemsID.length; i++){
				pbItemsID[i] -= 256;
			}
		} catch(Exception ex)
		{
			FMLLog.log(Level.SEVERE, ex, "Project Bench: Error encountered while loading config file.");
		} finally 
		{ 
			config.save(); 
		}
		if(VERSION_CHECK)
			VersionChecker.go();
	}
	
	@Init
	public void initMain(FMLInitializationEvent ev)
	{
		projectBench = new ProjectBenchBlock(pbID, Material.wood).setCreativeTab(CreativeTabs.tabDecorations);
		projectBenchUpgrade = new PBUpgradeItem(pbItemsID[0], 0).setUnlocalizedName("pbupi").setCreativeTab(CreativeTabs.tabMisc);
		projectBenchUpgradeII = new PBUpgradeItem(pbItemsID[1], 1).setUnlocalizedName("pbupii").setCreativeTab(CreativeTabs.tabMisc);
		craftingFrame = new ItemCraftingFrame(pbItemsID[2], EntityCraftingFrame.class).setUnlocalizedName("craftingframe");
		craftingFrameII = new ItemCraftingFrame(pbItemsID[3], EntityCraftingFrameII.class).setUnlocalizedName("craftingframeii");
		GameRegistry.registerBlock(projectBench, PBItemBlock.class, "pb_block");
		System.out.println("ProjectBench: Registered block id @ " +pbID +". Rendering: " +DO_RENDER +" @: " +SPEED_FACTOR);
		GameRegistry.registerTileEntity(TileEntityProjectBench.class, "bau5pbTileEntity");
		GameRegistry.registerTileEntity(TEProjectBenchII.class, "bau5pbTileEntityII");
		entityID[0] = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerModEntity(EntityCraftingFrame.class, "craftingFrame", entityID[0], this, 15, Integer.MAX_VALUE, false);
		entityID[1] = EntityRegistry.findGlobalUniqueEntityId();
		if(entityID[1] == entityID[0]){
			System.out.println("ProjectBench: Entity ID issue detected, resolving.");
			entityID[1] = entityID[0] + 1;
		}
		EntityRegistry.registerModEntity(EntityCraftingFrameII.class, "craftingFrameII", entityID[1] +1, this, 15, Integer.MAX_VALUE, false);
		proxy.registerRenderInformation();
		LanguageRegistry.addName(craftingFrame, "Advanced Crafting Frame");
		LanguageRegistry.addName(craftingFrameII, "Crafting Frame");
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		GameRegistry.addRecipe(new ItemStack(this.projectBench, 1, 0), new Object[]{
			" G ", "ICI", "WHW", 'G', Block.glass, 'I', Item.ingotIron, 'C', Block.workbench, 'W', Block.planks, 'H', Block.chest
		});
		GameRegistry.addRecipe(new ItemStack(this.projectBenchUpgrade, 1), new Object[]{
			" G ", "IWI", "WHW", 'G', Block.glass, 'I', Item.ingotIron, 'W', Block.planks, 'H', Block.chest
		});
		if(DEV_ENV){
			GameRegistry.addRecipe(new ItemStack(this.projectBench, 1, 1), new Object[]{
				"IPI", "WDW", "IWI", 'P', new ItemStack(this.projectBench, 1, 0), 'I', Item.ingotIron, 'D', Item.diamond, 'W', Block.planks
			});
			GameRegistry.addRecipe(new ItemStack(this.projectBenchUpgradeII, 1, 0), new Object[]{
				"IWI", "WDW", "IWI", 'I', Item.ingotIron, 'D', Item.diamond, 'W', Block.planks
			});
			GameRegistry.addRecipe(new ItemStack(this.craftingFrame), new Object[]{
				"SIS", "SCS", "SIS", 'C', Block.workbench, 'I', Item.ingotIron, 'S', Item.stick
			});
			GameRegistry.addRecipe(new ItemStack(this.craftingFrameII), new Object[]{
				"SGS", "RCR", "SSS", 'G', Item.silk, 'S', Item.stick, 'R', Item.redstone, 'C', Block.workbench
			});
		}		
	}
	@PostInit
	public void postInit(FMLPostInitializationEvent ev){
		if(DEV_ENV){
			new RecipeManager();
			System.out.println("**********************");
			System.out.println("* DEV ENV IS ACTIVE. *");
			System.out.println("**********************");
		}
	}
}
