package eu.uga.Config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	private HashSet<Long> _channels;
	
	@Expose
	@SerializedName("Custom emoji")
	private CustomEmoji _customEmoji;
	
	/***
	 * Getters -----------------------------------------------------------------------------------------------------------
	 */
	public String GetToken()
	{
		return _token;
	}
	
	public Set<Long> GetChannels()
	{
		return Collections.unmodifiableSet(_channels);
	}
	
	public CustomEmoji GetCustomEmoji()
	{
		return _customEmoji;
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
		config._channels = new HashSet<>();
		config._customEmoji = CustomEmoji.GetDefault();
		
		return config;
	}
}
