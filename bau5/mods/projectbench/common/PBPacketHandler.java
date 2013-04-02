package bau5.mods.projectbench.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;


public class PBPacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) 
	{
		ByteArrayDataInput bis = ByteStreams.newDataInput(packet.data);
		int id = bis.readInt();
		int i = bis.readInt();
		int j = bis.readInt();
		int k = bis.readInt();
		boolean hasStacks = bis.readByte() != 0;
		int[] result = null;
		if(hasStacks)
		{
			result = (id == 0) ? new int[27] : new int[54];
			for(int u = 0; u < result.length; u++)
			{
				result[u] = bis.readInt();
			}
		}
		
		World w = ProjectBench.instance.proxy.getClientSideWorld();			
//		System.out.println(w);
		if(w == null)
			return;
		TileEntity te = w.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityProjectBench)
		{
			TileEntityProjectBench tpb = (TileEntityProjectBench)te;
//			System.out.println("Got one for I");
			if(hasStacks)
				tpb.buildResultFromPacket(result);
			else
				tpb.setResult(null);
		}else if(te instanceof TEProjectBenchII){
			TEProjectBenchII tpb = (TEProjectBenchII)te;
//			System.out.println("Got one for II " +hasStacks +" " +tpb.worldObj.isRemote);
			if(hasStacks)
				tpb.buildResultFromPacket(result);
			else
				tpb.setListForDisplay(new ArrayList<ItemStack>());
		}
	}

	public static Packet prepPacketMkI(TileEntityProjectBench tpb)
	{
		int id = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int i = tpb.xCoord;
		int j = tpb.yCoord;
		int k = tpb.zCoord;
		int[] crafting = tpb.getRecipeStacksForPacket();
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
			dos.writeByte(hasStacks ? 1 : 0);
			if(hasStacks)
			{
				for(int u = 0; u < 27; u++)
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
	
	public static Packet prepPacketMkII(TEProjectBenchII tpb){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
		DataOutputStream dos = new DataOutputStream(bos);
		int id = 1;
		int i = tpb.xCoord;
		int j = tpb.yCoord;
		int k = tpb.zCoord;
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
