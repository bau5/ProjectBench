package com.bau5.projectbench.common.utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by bau5 on 5/21/2015.
 */
public class VersionCheckEventHandler{

    @SubscribeEvent
    public void playerJoinedWorld(EntityJoinWorldEvent ev){
        if(VersionChecker.didFail() || VersionChecker.isUpToDate()){
            MinecraftForge.EVENT_BUS.unregister(VersionCheckEventHandler.this);
        }else if(VersionChecker.isOutOfDate()) {
            if (ev.entity instanceof EntityPlayer && Reference.REMOTE_VERSION != null) {
                EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                EnumChatFormatting RED = EnumChatFormatting.RED;
                EntityPlayer player = ((EntityPlayer)ev.entity);
                postMessage(player, EnumChatFormatting.BLUE + "Project Bench: " + GRAY + "Update available.");
                postMessage(player,  " Version: " +RED + Reference.REMOTE_VERSION +GRAY + " Importance: " + RED +Reference.REMOTE_IMPORTANCE);
                postMessage(player, GRAY +" Changes: " + RED +Reference.CHANGES);
                MinecraftForge.EVENT_BUS.unregister(VersionCheckEventHandler.this);
            }
        }
    }

    private void postMessage(EntityPlayer pl, String message){
       pl.addChatComponentMessage(new ChatComponentText(message));
    }
}