package bau5.mods.projectbench.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet250CustomPayload;
import bau5.mods.projectbench.common.ContainerProjectBenchII;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MkIIWindowClick extends PBPacket{

    public int window_Id;
    public int inventorySlot;
    public int mouseClick;
    public short action;
    public ItemStack itemStack;
    public int origStackSize;
    public int holdingShift;
    
    public MkIIWindowClick() {};
    @SideOnly(Side.CLIENT)
	public MkIIWindowClick(int windowId, int par1, int par2, int par3, ItemStack itemstack, short short1, int size) {
    	super((byte)3);
		window_Id = windowId;
		inventorySlot = par1;
		mouseClick = par2;
		itemStack = itemstack != null ? itemstack.copy() : null;
		action = short1;
		holdingShift = par3;
		origStackSize = size;
	}
	
    @Override
    public Packet makePacket(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(24);
		DataOutputStream dos = new DataOutputStream(bos);
		try{
			dos.writeByte(PACKET_ID);
			dos.writeByte(window_Id);
	        dos.writeShort(inventorySlot);
	        dos.writeByte(mouseClick);
	        dos.writeShort(action);
	        dos.writeByte(holdingShift);
	        dos.writeInt((itemStack!=null)? itemStack.itemID : -1);
	        dos.writeInt((itemStack!=null)? itemStack.stackSize : -1);
	        dos.writeInt((itemStack!=null)? itemStack.getItemDamage() : -1);
	        dos.writeInt((itemStack!=null)? origStackSize : -1);
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
    
    @Override
    public void handlePacket(Packet250CustomPayload packet, Player player, ByteArrayDataInput bis){
    	window_Id = bis.readByte();
    	inventorySlot = bis.readShort();
    	mouseClick = bis.readByte();
    	action = bis.readShort();
    	holdingShift = bis.readByte();
    	int itemId = bis.readInt();
    	int stackSize = -1;
    	if(itemId != -1){
    		itemStack = new ItemStack(itemId, bis.readInt(), bis.readInt());
    		origStackSize = bis.readInt();
    	}
    	handleWindowClick(player);
    }
    public void handleWindowClick(Player player) {
    	EntityPlayerMP playerEntity = null;
    	if(player instanceof EntityPlayerMP)
    		playerEntity = (EntityPlayerMP)player;
        if (playerEntity!= null && playerEntity.openContainer.windowId == window_Id && playerEntity.openContainer.isPlayerNotUsingContainer(playerEntity))
        {
            if(playerEntity.openContainer instanceof ContainerProjectBenchII){
            	ItemStack origStack = null;
            	if(itemStack != null){
            		origStack = itemStack.copy();
            		origStack.stackSize = origStackSize;
            	}
            	ItemStack serverStack = ((ContainerProjectBenchII)playerEntity.openContainer).serverMouseClick(inventorySlot, mouseClick, holdingShift, playerEntity, origStack);
            	
            	if (ItemStack.areItemStacksEqual(itemStack, serverStack))
                {
                    playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(window_Id, action, true));
                    playerEntity.playerInventoryBeingManipulated = true;
                    playerEntity.openContainer.detectAndSendChanges();
                    playerEntity.updateHeldItem();
                    playerEntity.playerInventoryBeingManipulated = false;
                }
                else
                {
                	playerEntity.playerNetServerHandler.sendPacketToPlayer(PBPacketManager.getRejectionPacket(window_Id, action, false));
                    playerEntity.openContainer.setPlayerIsPresent(playerEntity, false);
                    ArrayList arraylist = new ArrayList();

                    for (int i = 0; i < playerEntity.openContainer.inventorySlots.size(); ++i)
                    {
                        arraylist.add(((Slot)playerEntity.openContainer.inventorySlots.get(i)).getStack());
                    }

                    playerEntity.sendContainerAndContentsToPlayer(playerEntity.openContainer, arraylist);
                }
        	}
        }
    }
}

    