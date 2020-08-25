package eu.uniga.Config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config
{
	/***
	 * Fields ------------------------------------------------------------------------------------------------------------
	 */
	@Expose
	@SerializedName("Token")
	private String _token;
	
	@Expose
	@SerializedName("Channels")
	private long[] _channels;
	
	@Expose
	@SerializedName("Integrated webserver port")
	private int _webserverPort;
	
	@Expose
	@SerializedName("Force reload resource pack")
	private boolean _forceReloadResourcePack;
	
	@Expose
	@SerializedName("Enable custom emoji related stuff")
	private boolean _emotesEnabled;
	
	/***
	 * Getters -----------------------------------------------------------------------------------------------------------
	 */
	public String GetToken()
	{
		return _token;
	}
	
	public long[] GetChannels()
	{
		return _channels;
	}
	
	public int GetWebserverPort()
	{
		return _webserverPort;
	}
	
	public boolean ShouldForceReloadResourcePack()
	{
		return _forceReloadResourcePack;
	}
	
	public boolean AreEmotesEnabled()
	{
		return _emotesEnabled;
	}
	
	/**
	 * Utils -------------------------------------------------------------------------------------------------------------
	 */
	public static Config GetConfig()
	{
		return Loader.GetConfig();
	}
	
	public static Config ReloadConfig()
	{
		return Loader.ReloadConfig();
	}
	
	static Config GetDefault()
	{
		Config config = new Config();
		
		config._token = "Insert discord token from https://discord.com/developers/applications/";
		config._channels = new long[0];
		config._webserverPort = 80;
		config._forceReloadResourcePack = false;
		config._emotesEnabled = true;
		
		return config;
	}
}
