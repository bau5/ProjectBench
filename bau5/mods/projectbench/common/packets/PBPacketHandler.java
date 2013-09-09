package bau5.mods.projectbench.common.packets;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.projectbench.common.tileentity.ContainerProjectBench;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 * 
 * PBPacketHandler
 *
 * @author _bau5
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

public class PBPacketHandler implements IPacketHandler
{
	public static String PACKET_CHANNEL = "bau5_PB";
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{
		if(packet.data == null)
			return;
		if(packet.data.length == 1){
			handlePBTinyPacket(packet, player);
			return;
		}
		ByteArrayDataInput bis = ByteStreams.newDataInput(packet.data);
		byte id = bis.readByte();
		switch(id){
		case 1: PBPacketManager.handleMkIPacket(packet, player, bis);
			return;
		case 2: PBPacketManager.handleMkIIPacket(packet, player, bis);
			return;
		case 3: PBPacketManager.handleMkIIWindowClick(packet, player, bis);
			return;
		case 4: PBPacketManager.handleRejectionPacket(packet, player, bis);
			return;
		case 5: PBPacketManager.handleRecipePacket(packet, player, bis);
			return;
		}
	}

	private void handlePBTinyPacket(Packet250CustomPayload packet, Player player) {
		byte tinyid = packet.data[0];
		switch(tinyid){
		case 1: if(player instanceof EntityPlayerMP) completeEmptyOfMatrix((EntityPlayerMP)player);
			return;
		case 2: if(player instanceof EntityPlayerMP) ((ContainerProjectBench)(((EntityPlayerMP) player).openContainer)).writePlanToNBT();
		}
	}

	private void completeEmptyOfMatrix(EntityPlayerMP thePlayer) {
        ArrayList itemListToSend = new ArrayList();
        ((ContainerProjectBench)thePlayer.openContainer).tileEntity.containerInit = true;
        for(int i = 0; i < 9; i++){
        	thePlayer.openContainer.transferStackInSlot(thePlayer, i + 1);
        }
        ((ContainerProjectBench)thePlayer.openContainer).tileEntity.containerInit = false;
        for (int i = 0; i < thePlayer.openContainer.inventorySlots.size(); ++i) {
            itemListToSend.add(((Slot) thePlayer.openContainer.inventorySlots.get(i)).getStack());
        }

        thePlayer.sendContainerAndContentsToPlayer(thePlayer.openContainer, itemListToSend);
        ((ContainerProjectBench)thePlayer.openContainer).tileEntity.findRecipe(false);
	}
}
