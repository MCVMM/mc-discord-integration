package eu.uniga.Config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomEmoji
{
	/***
	 * Fields ------------------------------------------------------------------------------------------------------------
	 */
	@Expose
	@SerializedName("Enable custom emoji")
	private boolean _enabled;
	
	@Expose
	@SerializedName("Integrated webserver port")
	private int _port;
	
	@Expose
	@SerializedName("Integrated webserver hostname")
	private String _hostname;
	
	@Expose
	@SerializedName("Integrated webserver reported port")
	private int _reportedPort;
	
	@Expose
	@SerializedName("Force reload resource pack")
	private boolean _forceReloadResourcePack;
	
	@Expose
	@SerializedName("Size")
	private int _size;
	
	/***
	 * Getters -----------------------------------------------------------------------------------------------------------
	 */
	public boolean AreEmotesEnabled()
	{
		return _enabled;
	}
	
	public int GetWebserverPort()
	{
		return _port;
	}
	
	public String GetWebserverHostname()
	{
		return _hostname;
	}
	
	public int GetWebserverReportedPort()
	{
		return _reportedPort;
	}
	
	public boolean ShouldForceReloadResourcePack()
	{
		return _forceReloadResourcePack;
	}
	
	public int GetSize()
	{
		return _size;
	}
	
	/**
	 * Utils -------------------------------------------------------------------------------------------------------------
	 */
	static CustomEmoji GetDefault()
	{
		CustomEmoji customEmoji = new CustomEmoji();
		
		customEmoji._port = 80;
		customEmoji._hostname = "localhost";
		customEmoji._reportedPort = 80;
		customEmoji._enabled = true;
		customEmoji._forceReloadResourcePack = true;
		customEmoji._size = 128;
		
		return customEmoji;
	}
}
