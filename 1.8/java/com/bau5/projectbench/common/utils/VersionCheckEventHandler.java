package com.bau5.projectbench.common.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
                String message = EnumChatFormatting.BLUE + "Project Bench: " +GRAY +"Update available.\n";
                message += " Version: " +RED + Reference.REMOTE_VERSION +GRAY + " Importance: " + RED +Reference.REMOTE_IMPORTANCE;
                message += GRAY +"\n Changes: " + RED +Reference.CHANGES;
                ((EntityPlayer) ev.entity).addChatComponentMessage(new ChatComponentText(message));
                MinecraftForge.EVENT_BUS.unregister(VersionCheckEventHandler.this);
            }
        }
    }
}