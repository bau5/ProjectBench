package bau5.mods.projectbench.common.packets;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.projectbench.common.tileentity.TEProjectBenchII;
import bau5.mods.projectbench.common.tileentity.TileEntityProjectBench;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PBPacketManager {
	/** ID:     Class:
	 *   1		  {@link MkIStandardPacket}
	 *   2		  {@link MKIIStandardPacket}
	 *   3     	  {@link MkIIWindowClick}
	 *   4		  {@link RejectionPacket}
	 *   5		  {@link RecipePacket}
	 */
	public static Packet getMkIPacket(TileEntityProjectBench tile) {
		return (Packet250CustomPayload)new MkIStandardPacket(tile).makePacket();
	}

	public static Packet getMkIIPacket(TEProjectBenchII tile) {
		return (Packet250CustomPayload)new MkIIStandardPacket(tile).makePacket();
	}
	
	public static Packet getMkIIWindowClick(int windowId, int par1, int par2, int par3, ItemStack itemstack, short short1, int stackSize){
		return (Packet250CustomPayload)new MkIIWindowClick(windowId, par1, par2, par3, itemstack, short1, stackSize).makePacket();
	}

	public static Packet getRejectionPacket(int window_Id, short action, ItemStack validStack, boolean b) {
		return (Packet250CustomPayload)new RejectionPacket(window_Id, action, validStack, b).makePacket();
	}
	
	public static Packet getRecipePacket(ItemStack theStack, boolean isEnabled){
		return (Packet250CustomPayload)new RecipePacket(theStack, isEnabled).makePacket();
	}
	
	public static void handleMkIPacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
		new MkIStandardPacket().handlePacket(packet, player, bis);
	}
	
	public static void handleMkIIPacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
		new MkIIStandardPacket().handlePacket(packet, player, bis);
	}
	
	public static void handleMkIIWindowClick(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
		 new MkIIWindowClick().handlePacket(packet, player, bis);
	}

	public static void handleRejectionPacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		new RejectionPacket().handlePacket(packet, player, bis);
	}

	public static void handleRecipePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		new RecipePacket().handlePacket(packet, player, bis);
	}
}
