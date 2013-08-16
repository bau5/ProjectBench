package bau5.mods.projectbench.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.tileentity.TEProjectBenchII;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class MkIIStandardPacket extends PBPacket {

	private TEProjectBenchII tpb;
	
	public MkIIStandardPacket(TEProjectBenchII tile){
		super((byte)2);
		tpb = tile;
	}
	
	public MkIIStandardPacket() { }

	@Override
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		int i = bis.readInt();
		int j = bis.readInt();
		int k = bis.readInt();
		byte d = bis.readByte();
		boolean hasStacks = bis.readByte() != 0;
		int[] result = null;
		if(hasStacks)
		{
			result = new int[54];
			for(int u = 0; u < result.length; u++)
			{
				result[u] = bis.readInt();
			}
		}
		
		World w = ProjectBench.proxy.getClientSideWorld();			
		if(w == null)
			return;
		TileEntity te = w.getBlockTileEntity(i, j, k);
		if(te instanceof TEProjectBenchII){
			TEProjectBenchII tpb = (TEProjectBenchII)te;
			tpb.setDirection(d);
			tpb.setNetworkModifying(true);
			if(hasStacks)
				tpb.buildResultFromPacket(result);
			else
				tpb.setListForDisplay(new ArrayList<ItemStack>());
			tpb.setNetworkModifying(false);
		}
	}
	
	@Override
	public Packet makePacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		byte id = PACKET_ID;
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
			dos.writeByte(id);
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
		packet.channel = this.channel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
		return packet;
	}
}
