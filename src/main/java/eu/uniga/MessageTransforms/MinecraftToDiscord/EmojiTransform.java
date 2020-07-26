package eu.uniga.MessageTransforms.MinecraftToDiscord;

import eu.uniga.MessageTransforms.SurrogatePairsDictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiTransform implements IMinecraftToDiscordTransform
{
	private final SurrogatePairsDictionary _emojiDictionary;
	private final Pattern _pattern = Pattern.compile("(:[a-zA-Z0-9\\-_]+?:)");
	
	public EmojiTransform(SurrogatePairsDictionary emojiDictionary)
	{
		_emojiDictionary = emojiDictionary;
	}
	
	@Override
	public String Transform(String text)
	{
		return TransformText(text);
	}
	
	private String TransformText(String text)
	{
		int lastIndex = 0;
		Matcher matcher = _pattern.matcher(text);
		StringBuilder builder = new StringBuilder(text.length());
		
		while (matcher.find())
		{
			String found = text.substring(matcher.start(), matcher.end());
			String foundEmote = _emojiDictionary.GetDiscordFromShortName(found);
			
			builder.append(text, lastIndex, matcher.start());
			
			if (foundEmote == null) builder.append(found);
			else builder.append(foundEmote);
			
			lastIndex = matcher.end();
		}
		if (lastIndex < text.length())
		{
			builder.append(text.substring(lastIndex));
		}
		
		return builder.toString();
	}
}
