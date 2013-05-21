package bau5.mods.projectbench.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
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

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{
		if(packet.data[0] == 1 && packet.data.length == 1){
			completeEmptyOfMatrix((EntityPlayerMP)player);
			return;
		}
		ByteArrayDataInput bis = ByteStreams.newDataInput(packet.data);
		int id = bis.readInt();
		int i = bis.readInt();
		int j = bis.readInt();
		int k = bis.readInt();
		byte d = bis.readByte();
		boolean hasStacks = bis.readByte() != 0;
		int[] result = null;
		if(hasStacks)
		{
			result = (id == 0) ? new int[3] : new int[54];
			for(int u = 0; u < result.length; u++)
			{
				result[u] = bis.readInt();
			}
		}
		
		World w = ProjectBench.proxy.getClientSideWorld();			
		if(w == null)
			return;
		TileEntity te = w.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityProjectBench)
		{
			TileEntityProjectBench tpb = (TileEntityProjectBench)te;
			if(hasStacks)
				tpb.buildResultFromPacket(result);
			else
				tpb.setResult(null);
//			else
//				tpb.setResult(null);
		}else if(te instanceof TEProjectBenchII){
			TEProjectBenchII tpb = (TEProjectBenchII)te;
			tpb.setDirection(d);
			if(hasStacks)
				tpb.buildResultFromPacket(result);
			else
				tpb.setListForDisplay(new ArrayList<ItemStack>());
		}
	}

	public void completeEmptyOfMatrix(EntityPlayerMP thePlayer) {
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

	public static Packet prepPacketMkI(TileEntityProjectBench tpb)
	{
		int id = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int i = tpb.xCoord;
		int j = tpb.yCoord;
		int k = tpb.zCoord;
		int d = 6;
//		int[] crafting = tpb.getRecipeStacksForPacket();
		int[] result = new int[3];
		if(tpb.getResult() != null){
			result[0] = tpb.getResult().itemID;
			result[1] = tpb.getResult().stackSize;
			result[2] = tpb.getResult().getItemDamage();
		}
			
		boolean hasStacks = false;
		if(result != null)
		{
			 hasStacks = true;
		}
		try 
		{
			dos.writeInt(id);
			dos.writeInt(i);
			dos.writeInt(j);
			dos.writeInt(k);
			dos.writeByte(d);
			dos.writeByte(hasStacks ? 1 : 0);
			if(hasStacks)
			{
//				for(int u = 0; u < 27; u++)
//				{
//					dos.writeInt(crafting[u]);
//				}
				for(int u = 0; u < 3; u++)
					dos.writeInt(result[u]);
			}
		} catch(IOException ex)
		{
			FMLLog.log(Level.SEVERE, ex, "Project Bench: failed packet prepping.");
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "bau5_PB";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
		
		return packet;
	}
	
	public static Packet prepPacketMkII(TEProjectBenchII tpb){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int id = 1;
		int i = tpb.xCoord;
		int j = tpb.yCoord;
		int k = tpb.zCoord;
		int d = tpb.getDirection();
		int[] crafting = tpb.getInputStacksForPacket();
		boolean hasStacks = false;
		if(crafting != null)
		{
			 hasStacks = true;
		}
		try 
		{
			dos.writeInt(id);
			dos.writeInt(i);
			dos.writeInt(j);
			dos.writeInt(k);
			dos.writeByte(d);
			dos.writeByte(hasStacks ? 1 : 0);
			if(hasStacks)
			{
				for(int u = 0; u < 54; u++)
				{
					dos.writeInt(crafting[u]);
				}
			}
		} catch(IOException ex)
		{
			FMLLog.log(Level.SEVERE, ex, "Project Bench: failed packet prepping.");
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "bau5_PB";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
		
		return packet;
	}	
}
