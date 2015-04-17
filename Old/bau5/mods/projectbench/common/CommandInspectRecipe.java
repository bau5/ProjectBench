package bau5.mods.projectbench.common;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

public class CommandInspectRecipe extends CommandBase {
	private final String COMMAND_LOCALE = "command.bau5ProjectBench.pbinspect.";
	
	@Override
	public String getCommandName() {
		return "pb" +"inspect"/*StatCollector.translateToLocal(COMMAND_LOCALE+"name")*/;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" +getCommandName() +"<"+"item ID"/*StatCollector.translateToLocal(COMMAND_LOCALE+"part3")*/+">" +"[meta]"/*StatCollector.translateToLocal(COMMAND_LOCALE+"part4")*/;
	}
	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		ItemStack theStack = null;
		if(astring.length > 2)
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		else if(astring.length == 0){
			if(!(sender instanceof EntityPlayer))
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			ItemStack held = ((EntityPlayer)sender).getCurrentEquippedItem();
			if(held == null){
				sender.sendChatToPlayer(ChatMessageComponent.func_111066_d("Must be holding an item."/*StatCollector.translateToLocal(COMMAND_LOCALE +"err1")*/));
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			}else{
				theStack = held.copy();
			}
		}else{
			int theID = Integer.parseInt(astring[0]);
			int meta = 0;
			if(astring.length == 2)
				meta = Integer.parseInt(astring[1]);
			theStack = new ItemStack(theID, 1, meta);
		}
		RecipeItem theRec = RecipeManager.instance().searchForRecipe(theStack, true);
		if(theRec == null){
			sender.sendChatToPlayer(ChatMessageComponent.func_111066_d("No recipe found for item"/*StatCollector.translateToLocal(COMMAND_LOCALE +"err2")*/ +" "+theStack.itemID +":" +theStack.getItemDamage() +"."));
		}else{
			ArrayList<ItemStack[]> ls = theRec.alternatives();
			StringBuilder builder = new StringBuilder();
			builder.append("Recipe for"/*StatCollector.translateToLocal(COMMAND_LOCALE +"part1")*/ +" "+EnumChatFormatting.DARK_RED +theRec.result() +EnumChatFormatting.WHITE  +" Enabled? " +theRec.isEnabled() +"\n ");
			builder.append("Alts:"/*StatCollector.translateToLocal(COMMAND_LOCALE +"part2")*/ +" "+ls.size() +"\n");
			for(int i = 0; i < ls.size(); i++){
				builder.append("   "+(i+1) +": ");
				for(ItemStack stack : ls.get(i)){
					builder.append(stack +" ");
				}
				builder.append("\n");
			}
			String theString = builder.toString();
			sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(theString));
			try{
				StringSelection selection = new StringSelection(theString);
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.setContents(selection, selection);
			}catch(Exception ex){
				
			}
		}
	}
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
}
