package com.bau5.projectbench.common;

import com.bau5.projectbench.common.inventory.ContainerProjectBench;
import com.bau5.projectbench.common.utils.PlanHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by bau5 on 4/17/2015.
 */
public class SimpleMessage implements IMessage {

    private int id;
    private int dim;
    private int x, y, z;

    public SimpleMessage(){}

    public SimpleMessage(int id, int dimension, BlockPos pos){
        this.id = id;
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        dim = dimension;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        dim = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dim);
    }

    public static class Handler implements IMessageHandler<SimpleMessage, IMessage>{

        @Override
            public IMessage onMessage(SimpleMessage message, MessageContext ctx) {
                switch(message.id){
                    case 0: emptyCraftMatrix(ctx.getServerHandler().player);
                        break;
                    case 1:
                        TileEntityProjectBench tile = ((ContainerProjectBench) ctx.getServerHandler().player.openContainer).getTileEntity();
                        PlanHelper.writePlan(tile);
                        break;
                }
                return null;
        }

        private void emptyCraftMatrix(EntityPlayerMP thePlayer) {
            if(!(thePlayer.openContainer instanceof ContainerProjectBench))
                return;
            for(int i = 0; i < 9; i++) {
                thePlayer.openContainer.transferStackInSlot(thePlayer, 37 + i);
            }
        }

    }
}
