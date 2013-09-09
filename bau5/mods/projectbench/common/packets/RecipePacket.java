package bau5.mods.projectbench.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ChatMessageComponent;
import bau5.mods.projectbench.common.recipes.RecipeManager;
import bau5.mods.projectbench.common.recipes.RecipeManager.RecipeItem;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class RecipePacket extends PBPacket {
	
	private ItemStack theStack;
	private boolean   enabled;
	
	public RecipePacket() {}
	public RecipePacket(ItemStack stack, boolean b){
		super((byte)5);
		theStack = stack;
		enabled = b;
	}
	
	@Override
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		int id = bis.readInt();
		int stackSize = bis.readInt();
		int meta = bis.readInt();
		boolean enable = (bis.readByte() == 1 ? true : false);
		RecipeItem rec = RecipeManager.instance().searchForRecipe(new ItemStack(id, stackSize, meta), true);
		if(rec != null){
			if(enable)
				rec.forceEnable();
			else
				rec.forceDisable();
			((ICommandSender)player).sendChatToPlayer(ChatMessageComponent.func_111066_d("Recipe " +rec.toString() +" has been "+(enable ? "enabled." : "disabled.")));
		}
	}
	
	@Override
	public Packet makePacket() {
		byte id = PACKET_ID;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream dos = new DataOutputStream(bos);
		int[] theStackNumbers = new int[] { 
			theStack.itemID, theStack.stackSize, theStack.getItemDamage()
			};
		try{
			dos.writeByte(id);
			for(int i : theStackNumbers)
				dos.writeInt(i);
			dos.writeByte((enabled ? 1 : 0));
		}catch(IOException ex){
			FMLLog.log(Level.SEVERE, ex, "Project Bench: failed prepping RecipePacket");
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = this.channel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
		return packet;
	}

}
