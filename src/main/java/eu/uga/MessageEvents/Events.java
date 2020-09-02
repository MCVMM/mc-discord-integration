package eu.uga.MessageEvents;

/**
 * Static class for holding callback(s) for mixins
 */
public class Events
{
	private static IMinecraftChatMessage _minecraftChatMessage;
	
	public static void SetMinecraftChatMessageCallback(IMinecraftChatMessage minecraftChatMessage)
	{
		_minecraftChatMessage = minecraftChatMessage;
	}
	
	public static IMinecraftChatMessage GetChatMessageCallback()
	{
		return _minecraftChatMessage;
	}
}
