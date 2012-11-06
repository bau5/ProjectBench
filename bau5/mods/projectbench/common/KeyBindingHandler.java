package bau5.mods.projectbench.common;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBindingHandler extends KeyHandler 
{

	private static KeyBinding keyN = new KeyBinding("Test", Keyboard.KEY_N);
	private Minecraft mc = FMLClientHandler.instance().getClient();
	
	public KeyBindingHandler() 
	{
		super(new KeyBinding[] { keyN } , new boolean[] {false});
		System.out.println("Registered: " + keyBindings[0].keyCode);
	}

	@Override
	public String getLabel()
	{
		return "bau5_craftingHelper";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) 
	{
		System.out.println("meow");
		if(mc.currentScreen instanceof GuiContainer)
		{
			FMLClientHandler.instance().haltGame("RuhRuoh", new Throwable("meowing"));
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) 
	{
		System.out.println("meow");
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return null;
	}

}
