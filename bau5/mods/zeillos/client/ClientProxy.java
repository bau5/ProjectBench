package bau5.mods.zeillos.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.world.World;
import bau5.mods.zeillos.common.CommonProxy;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		
	}
	@Override
	public World getClientSideWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
