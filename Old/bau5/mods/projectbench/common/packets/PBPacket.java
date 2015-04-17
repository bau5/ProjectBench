package bau5.mods.projectbench.common.packets;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PBPacket extends Packet250CustomPayload {
	public byte PACKET_ID = 127;
	public PBPacket() {}
	public PBPacket(byte id){
		channel = PBPacketHandler.PACKET_CHANNEL;
		PACKET_ID = id;
	}
	public PBPacket(byte[] bs) {
		super(PBPacketHandler.PACKET_CHANNEL, bs);
	}
	public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis) {
		System.out.println("ERROR: Incorrect usage of PBPacket reading.");
	}
	public Packet makePacket(){
		System.out.println("ERROR: Incorrect usage of PBPacket writing.");
		return new Packet250CustomPayload(channel, new byte[]{0});
	}
}
