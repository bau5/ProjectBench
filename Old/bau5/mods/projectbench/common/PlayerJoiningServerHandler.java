package bau5.mods.projectbench.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import bau5.mods.projectbench.common.packets.PBPacketManager;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayerJoiningServerHandler {
	@ForgeSubscribe
	@SideOnly(Side.SERVER)
	public void entityJoining(EntityJoinWorldEvent ev){
		if(ev.entity instanceof EntityPlayerMP){
			ArrayList<RecipeItem> list = RecipeManager.instance().getDisabledRecipes();
			for(RecipeItem rec : list)
				if(!rec.isEnabled())
					PacketDispatcher.sendPacketToPlayer(PBPacketManager.getRecipePacket(rec.result(), false), (Player)ev.entity);
		}
	}

}
