package eu.uniga.MessageTransforms;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

public class FormattingContext
{
	private final JDA _client;
	private final SurrogatePairsDictionary _dictionary;
	
	public FormattingContext(@Nonnull JDA client, @Nonnull SurrogatePairsDictionary dictionary)
	{
		_client = client;
		_dictionary = dictionary;
	}
	
	public SurrogatePairsDictionary GetDictionary()
	{
		return _dictionary;
		//return DiscordIntegrationMod.dictionary();
	}
	
	public JDA GetClient()
	{
		return _client;
		//return DiscordIntegrationMod.bot().client();
	}
}