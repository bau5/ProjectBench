package bau5.mods.projectbench.client;

import net.minecraft.world.World;
import bau5.mods.projectbench.common.CommonProxy;
import bau5.mods.projectbench.common.EntityCraftingFrame;
import bau5.mods.projectbench.common.TEProjectBenchII;
import bau5.mods.projectbench.common.TileEntityProjectBench;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderInformation() {
		ClientRegistry.bindTileEntitySpecialRenderer(
				TileEntityProjectBench.class, new TEProjectBenchRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(
				TEProjectBenchII.class, new TEProjectBenchIIRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityCraftingFrame.class, new RenderCraftingFrame());
	}

	@Override
	public World getClientSideWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}