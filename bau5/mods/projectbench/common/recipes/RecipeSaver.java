package bau5.mods.projectbench.common.recipes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ForgeSubscribe;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class RecipeSaver {
	
	public static File getSaveFile(){
		String dir = "asdf";
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			dir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
			dir = dir.substring(0, dir.lastIndexOf('.'));
			dir = dir + "/saves/";
		}
		else{
			Object obj = ReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "canonicalConfigDir");
			if(obj != null && obj instanceof File){
				String str = ((File)obj).getAbsolutePath();
				dir = str.substring(0, str.lastIndexOf('\\')) +"\\";
			}
		}
			
		return new File(dir + MinecraftServer.getServer().getFolderName() + "/recipes.dat");
	}
	
	public static void writeToNBT(){
		try{
			File f = getSaveFile();
			if(!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(fos);
			
			ArrayList<RecipeItem> disabledRecipes = RecipeManager.instance().getDisabledRecipes();
			NBTTagCompound nbtcmpnd = new NBTTagCompound();
			NBTTagList tagList = new NBTTagList();
			int num = 0;
			for(RecipeItem rec : disabledRecipes){
				ItemStack result = rec.result();
				if(result != null){
					NBTTagCompound tag = new NBTTagCompound();
					result.writeToNBT(tag);
					tagList.appendTag(tag);
					num++;
				}
			}
			nbtcmpnd.setTag("DisabledRecipes", tagList);
			System.out.println("Project Bench: Successfully saved " +num +" disabled recipes.");
			
			CompressedStreamTools.write(nbtcmpnd, dos);
			fos.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void readFromNBT(){
		try{
			File f = getSaveFile();
			if(!f.exists()){
				f.createNewFile();
				if(ProjectBench.VERBOSE){
					System.out.println("Project Bench: loading of saved disabled stacks halted, no save file.");
				}
				RecipeManager.instance().initiateRecipeManager();
				return;
			}
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			NBTTagCompound nbtcmpnd = CompressedStreamTools.read(dis);
			NBTTagList tagList = nbtcmpnd.getTagList("DisabledRecipes");
			int num = 0;
			for(int i = 0; i < tagList.tagCount(); i++){
				NBTBase base = tagList.tagAt(i);
				if(base != null && base instanceof NBTTagCompound){
					NBTTagCompound tag = (NBTTagCompound)base;
					ItemStack theStack = ItemStack.loadItemStackFromNBT(tag);
					if(theStack != null){
						RecipeItem rec = RecipeManager.instance().searchForRecipe(theStack, true);
						if(rec != null){
							if(RecipeCrafter.checkItemMatch(rec.result(), theStack, false)){
								if(ProjectBench.VERBOSE)
									System.out.println("Project Bench: disabling " +rec.toString() +" from save.");
								rec.forceDisable();
								num++;
							}
						}
					}
				}
			}
			System.out.println("Project Bench: Successfully loaded " +num +" disabled recipes from save.");
			
			fis.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
