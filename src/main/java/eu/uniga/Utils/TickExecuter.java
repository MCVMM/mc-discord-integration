package eu.uniga.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TickExecuter
{
	public interface IFunction
	{
		void Call();
	}
	
	private final List<IFunction> _buffer;
	private final Logger _logger;
	
	public TickExecuter()
	{
		_logger = LogManager.getLogger();
		_buffer = new ArrayList<>();
	}
	
	public void Add(IFunction action)
	{
		synchronized (_buffer)
		{
			_buffer.add(action);
		}
	}
	
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
