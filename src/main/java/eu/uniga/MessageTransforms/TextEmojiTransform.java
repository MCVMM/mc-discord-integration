package eu.uniga.MessageTransforms;

import eu.uniga.EmojiService.SurrogatePairsDictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEmojiTransform
{
	private final SurrogatePairsDictionary _emojiDictionary;
	private final Pattern _pattern = Pattern.compile("(:[a-zA-Z0-9\\-_]+?:)");
	
	public TextEmojiTransform(SurrogatePairsDictionary emojiDictionary)
	{
		_emojiDictionary = emojiDictionary;
	}
	
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
