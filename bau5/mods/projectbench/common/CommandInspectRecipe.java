package bau5.mods.projectbench.common;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

public class CommandInspectRecipe extends CommandBase {
	@Override
	public String getCommandName() {
		return "pbinspect";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" +getCommandName() + " <itemID> [meta]";
	}
	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if(astring.length > 2 || astring.length == 0)
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		int theID = Integer.parseInt(astring[0]);
		int meta = 0;
		if(astring.length == 2)
			meta = Integer.parseInt(astring[1]);
		ItemStack theStack = new ItemStack(theID, 1, meta);
		RecipeItem theRec = RecipeManager.instance().searchForRecipe(theStack);
		if(theRec == null){
			sender.sendChatToPlayer("No recipe found for item " +theID +":" +meta +".");
		}else{
			String red = "\u00A7c";
			String grey= "\u00A78";
			ArrayList<ItemStack[]> ls = theRec.alternatives();
			StringBuilder builder = new StringBuilder();
			builder.append(theRec.result() +"\n ");
			builder.append("Alts: " +ls.size() +"\n  ");
			for(int i = 0; i < ls.size(); i++){
				builder.append(" "+(i+1) +": ");
				for(ItemStack stack : ls.get(i)){
					builder.append(stack +" ");
				}
				builder.append("\n");
			}
			String theString = builder.toString();
			sender.sendChatToPlayer(theString);
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
