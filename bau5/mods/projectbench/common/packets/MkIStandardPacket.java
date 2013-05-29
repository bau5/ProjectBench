package bau5.mods.projectbench.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import bau5.mods.projectbench.common.ProjectBench;
import bau5.mods.projectbench.common.TileEntityProjectBench;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class MkIStandardPacket extends PBPacket {

	private TileEntityProjectBench tpb;
	
	public MkIStandardPacket() {}
	
	public MkIStandardPacket(TileEntityProjectBench tile){
		super((byte)1);
		tpb = tile;
	}
	
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
			result = new int[3];
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
		}
	}
	
	@Override
	public Packet makePacket() {
		byte id = PACKET_ID;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int i = tpb.xCoord;
		int j = tpb.yCoord;
		int k = tpb.zCoord;
		int d = 6;
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
			dos.writeByte(id);
			dos.writeInt(i);
			dos.writeInt(j);
			dos.writeInt(k);
			dos.writeByte(d);
			dos.writeByte(hasStacks ? 1 : 0);
			if(hasStacks)
			{
				for(int u = 0; u < 3; u++)
					dos.writeInt(result[u]);
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
