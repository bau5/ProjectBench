package bau5.mods.projectbench.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

public class RejectionPacket extends PBPacket {
	private int win_id;
	private short theAction;
	private boolean accepted;
	
	public RejectionPacket() {}
	public RejectionPacket(int id, short theAct, boolean accept){
		super((byte)4);
		win_id = id;
		theAction = theAct;
		accepted  = accept;
	}
	
	@Override
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		
	}
	
	@Override
	public Packet makePacket(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream dos = new DataOutputStream(bos);
		try{
			dos.writeByte(PACKET_ID);
			dos.writeInt(win_id);
	        dos.writeShort(theAction);
	        dos.writeByte((accepted) ? 1 : 0);
		}catch (IOException ex){
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
