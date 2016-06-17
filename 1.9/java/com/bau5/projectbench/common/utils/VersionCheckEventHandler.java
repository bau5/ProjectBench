package com.bau5.projectbench.common.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
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

            if (ev.getEntity() instanceof EntityPlayer && Reference.REMOTE_VERSION != null) {
                ChatFormatting GRAY = ChatFormatting.GRAY;
                ChatFormatting RED = ChatFormatting.RED;
                String message = ChatFormatting.BLUE + "Project Bench: " +GRAY +"Update available.\n";
                message += " Version: " +RED + Reference.REMOTE_VERSION +GRAY + " Importance: " + RED +Reference.REMOTE_IMPORTANCE;
                message += GRAY +"\n Changes: " + RED +Reference.CHANGES;
                ((EntityPlayer) ev.getEntity()).addChatComponentMessage(new TextComponentString(message));
                MinecraftForge.EVENT_BUS.unregister(VersionCheckEventHandler.this);
            }
        }
    }
}