package eu.uniga.MessageTransforms;

import eu.uniga.DiscordIntegrationMod;

import java.util.HashMap;
import java.util.Map;

public class SurrogatePairsDictionary
{
	private final Map<String, Integer> _discordToSurrogatePairDynamic = new HashMap<>();
	private final Map<String, String> _shortNameToDiscordDynamic = new HashMap<>();
	private final Map<String, String> _discordToShortNameDynamic = new HashMap<>();
	private final Map<String, Integer> _shortNameToSurrogatePairDynamic = new HashMap<>();
	//private static Map<Integer, String> _SPToSN;
	//private static Map<String, Integer> _SNToSP;
	
	public void Set(Map<String, Integer> dictionary)
	{
		_discordToSurrogatePairDynamic.clear();
		
		dictionary.forEach(this::AddToDictionaries);
	}
	
	private void AddToDictionaries(String discordString, Integer surrogatePair)
	{
		int first = discordString.indexOf(':');
		int last = discordString.lastIndexOf(':') + 1;
		String oldShortName = discordString.substring(first, last);
		String shortName = oldShortName;
		int attempt = 0;
		
		while (_shortNameToDiscordDynamic.containsKey(shortName)) shortName = oldShortName + "-" + ++attempt;
		
		_discordToSurrogatePairDynamic.put(discordString, surrogatePair);
		_shortNameToDiscordDynamic.put(shortName, discordString);
		_discordToShortNameDynamic.put(discordString, shortName);
		_shortNameToSurrogatePairDynamic.put(shortName, surrogatePair);
	}
	
	// TODO: static emotes
	public Integer GetSurrogatePairFromShortName(String shortName)
	{
		return _shortNameToSurrogatePairDynamic.get(shortName);
	}
	
	public Integer GetSurrogatePairFromDiscord(String shortName)
	{
		return _discordToSurrogatePairDynamic.get(shortName);
	}
	
	public String GetShortNameFromDiscord(String discord)
	{
		return _discordToShortNameDynamic.get(discord);
	}
	
	public String GetDiscordFromShortName(String shortName)
	{
		return _shortNameToDiscordDynamic.get(shortName);
	}
	
	
	/*
	static
	{
		_SPToSN = new HashMap<>();
		_SNToSP = new HashMap<>();
	}*/
}
