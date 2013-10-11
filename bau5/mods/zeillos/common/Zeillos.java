package bau5.mods.zeillos.common;


import java.util.logging.Level;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid=Reference.MOD_ID, version=Reference.VERSION, name=Reference.NAME)
@NetworkMod(serverSideRequired=false, clientSideRequired = true)/*,
			channels={Reference.CHANNEL}, packetHandler=)*/
public class Zeillos {

	@Instance(Reference.MOD_ID)
	public static Zeillos instance;
	@SidedProxy(clientSide="bau5.mods.zeillos.client.ClientProxy",
				serverSide="bau5.mods.zeillos.common.CommonProxy")
	public static CommonProxy proxy;
	
	public Item magicMirror;
	
	private int[] itemIDs = new int[1];
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev){
		Configuration cfg = new Configuration(ev.getSuggestedConfigurationFile());
		try{
			cfg.load();
			itemIDs[0] = cfg.getItem(Configuration.CATEGORY_ITEM, "Magic Mirror", 20190).getInt(20190);
		}catch(Exception ex){
			FMLLog.log(Level.SEVERE, ex, "Zeillos: Failed loading configuration file.");
		}finally{
			cfg.save();
		}
		initParts();
	}
	public void initParts(){
		magicMirror = new ItemMagicMirror(itemIDs[0]).setCreativeTab(CreativeTabs.tabMisc).setUnlocalizedName("bz_mm");
	}
	@EventHandler
	public void initMain(FMLInitializationEvent ev){
	}
}
