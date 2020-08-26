package eu.uniga.Utils;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread safe way to execute code on server thread
 */
public class TickExecuter
{
	public interface IFunction
	{
		void Call(MinecraftServer minecraftServer);
	}
	
	private final List<IFunction> _queued = new ArrayList<>();
	private List<IFunction> _everyTick = new ArrayList<>();
	private final Object _everyTickLock = new Object();
	
	// Thread safe
	public void ExecuteNextTick(IFunction function)
	{
		synchronized (_queued)
		{
			_queued.add(function);
		}
	}
	
	public void AddToExecuteEveryTick(IFunction function)
	{
		List<IFunction> newEveryTick = new ArrayList<>();
		
		synchronized (_everyTickLock)
		{
			newEveryTick.addAll(_everyTick);
			newEveryTick.add(function);
			_everyTick = newEveryTick;
		}
	}
	
	public void RemoveFromExecuteEveryTick(IFunction function)
	{
		List<IFunction> newEveryTick = new ArrayList<>();
		
		synchronized (_everyTickLock)
		{
			_everyTick.forEach(oldFunction -> { if (oldFunction != function) newEveryTick.add(oldFunction); });
			_everyTick = newEveryTick;
		}
	}
	
	// Call from server thread
	public void RunAll(MinecraftServer minecraftServer)
	{
		List<IFunction> backup = _everyTick;
		backup.forEach(function -> function.Call(minecraftServer));
		
		synchronized (_queued)
		{
			_queued.forEach(function -> function.Call(minecraftServer));
			_queued.clear();
		}
	}
}
