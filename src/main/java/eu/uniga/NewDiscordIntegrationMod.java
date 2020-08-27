package eu.uniga;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class NewDiscordIntegrationMod implements ModInitializer
{
	private Thread _mod;
	
	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTING.register(this::Start);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::Stop);
		ServerTickEvents.START_SERVER_TICK.register(this::Tick);
	}
	
	private void Start(MinecraftServer minecraftServer)
	{
		_mod = new Thread(DiscordIntegrationMod.CreateInstance(minecraftServer));
		_mod.start();
	}
	
	private void Stop(MinecraftServer minecraftServer)
	{
		_mod.interrupt();
	}
	
	private void Tick(MinecraftServer minecraftServer)
	{
		DiscordIntegrationMod.GetInstance().MinecraftTick(minecraftServer);
	}
}