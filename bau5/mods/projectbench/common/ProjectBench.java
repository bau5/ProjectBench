package bau5.mods.projectbench.common;

import java.util.logging.Logger;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;


import cpw.mods.fml.common.Loader;
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

@Mod (modid = "bau5_ProjectBench", name = "Project Bench", version = "0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ProjectBench 
{
	@Instance
	public static ProjectBench instance;
	@SidedProxy(clientSide = "bau5.mods.projectbench.client.ClientProxy",
				serverSide = "bau5.mods.projectbench.common.CommonProxy")
	public static CommonProxy proxy;
	
	public Block projectBench;
	public static String baseTexFile = "/pb_resources";
	public static String textureFile = baseTexFile + "/pbsheet.png";
	
	@PreInit
	public void preInit(FMLPreInitializationEvent ev)
	{
		
	}

	@Init
	public void initMain(FMLInitializationEvent ev)
	{
		proxy.registerRenderInformation();
		projectBench = new ProjectBenchBlock(700, Material.wood);
		GameRegistry.registerBlock(projectBench);
		GameRegistry.registerTileEntity(TileEntityProjectBench.class, "bau5pbTileEntity");
		LanguageRegistry.addName(projectBench, "Project Bench");
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		GameRegistry.addRecipe(new ItemStack(this.projectBench, 1), new Object[]{
			"#", '#', Block.dirt
		});
	}
}
