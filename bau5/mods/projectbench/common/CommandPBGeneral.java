package bau5.mods.projectbench.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

public class CommandPBGeneral extends CommandBase {
	@Override
	public String getCommandName() {
		return "pb";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" +getCommandName() + " <(disable|enable <itemID [meta]>)> <reload>";
	}
	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if(astring.length == 2 || astring.length == 3){
			if(astring[0].equals("enable") || astring[0].equals("disable")){
				boolean flag = astring[0].equals("enable");
				int meta = 0;
				if(astring.length == 3)
					meta = Integer.parseInt(astring[2]);
				RecipeItem rec = RecipeManager.instance().searchForRecipe(new ItemStack(Integer.parseInt(astring[0]), 1, meta));
				if(rec != null){
					if(flag)
						rec.forceEnable();
					else
						rec.forceDisable();
				}else{
					sender.sendChatToPlayer(new ChatMessageComponent().func_111066_d("Recipe for item not found."));
					return;
				}
			}
		}else if(astring.length == 1){
			if(astring[0].equals("reload")){
				RecipeManager.instance().initiateRecipeManager();
			}else
				throw new WrongUsageException(this.getCommandUsage(sender), new Object());
		}else{
			throw new WrongUsageException(this.getCommandUsage(sender), new Object());
		}
	}
	@Override
	public int getRequiredPermissionLevel() {
		return (MinecraftServer.getServer().isDedicatedServer() ? 5 : 4);
	}
}
