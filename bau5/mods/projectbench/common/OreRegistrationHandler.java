package bau5.mods.projectbench.common;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class OreRegistrationHandler {
    private static HashMap<Integer, ArrayList<ItemStack>> oreStacks = new HashMap<Integer, ArrayList<ItemStack>>();

	@ForgeSubscribe
	public void onOreRegistry(OreRegisterEvent event){
		int id = OreDictionary.getOreID(event.Ore);
		ArrayList<ItemStack> ores = oreStacks.get(id);
		if(ores != null)
			ores.add(event.Ore);
		else{
			ores = new ArrayList<ItemStack>();
			ores.addAll(OreDictionary.getOres(id));
			oreStacks.put(id, ores);
		}
	}  
	public static HashMap<Integer, ArrayList<ItemStack>> getOreAlternatives(){
		return oreStacks;
	}
}
