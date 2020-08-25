package eu.uniga.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread safe way to execute code on server thread
 */
public class TickExecuter
{
	public interface IFunction
	{
		void Call();
	}
	
	private final List<IFunction> _buffer;
	
	public TickExecuter()
	{
		_buffer = new ArrayList<>();
	}
	
	// Thread safe
	public void ExecuteNextTick(IFunction action)
	{
		synchronized (_buffer)
		{
			_buffer.add(action);
		}
	}
	
	// Call from server threa
	public void RunAll()
	{
		synchronized (_buffer)
		{
			for (IFunction action : _buffer)
			{
				action.Call();
			}
			
			_buffer.clear();
		}
	}
}
