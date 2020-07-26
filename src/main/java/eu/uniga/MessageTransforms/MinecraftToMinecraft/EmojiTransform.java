package eu.uniga.MessageTransforms.MinecraftToMinecraft;

import eu.uniga.MessageTransforms.SurrogatePairsDictionary;
import eu.uniga.Utils.CodePoints;
import eu.uniga.Utils.MinecraftStyle;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiTransform implements IMessageTransform
{
	private final SurrogatePairsDictionary _emojiDictionary;
	private final Pattern _pattern = Pattern.compile("(:[a-zA-Z0-9\\-_]+?:)");
	
	public EmojiTransform(SurrogatePairsDictionary emojiDictionary)
	{
		_emojiDictionary = emojiDictionary;
	}
	
	@Override
	public TranslatableText Transform(TranslatableText text)
	{
		return TransformText(text);
	}
	
	private MutableText TransformTextRec(MutableText text)
	{
		List<Text> oldSiblings = new ArrayList<>(text.getSiblings().size());
		oldSiblings.addAll(text.getSiblings());
		text.getSiblings().clear();
		
		LiteralText out = null;
		
		int lastIndex = 0;
		String textPart = text.getString();
		Matcher matcher = _pattern.matcher(textPart);
		
		while (matcher.find())
		{
			String pre = textPart.substring(lastIndex, matcher.start());
			String found = textPart.substring(matcher.start(), matcher.end());
			Integer foundEmote = _emojiDictionary.GetSurrogatePairFromShortName(found);
			
			if (foundEmote == null)
			{
				pre += found;
				
				if (out == null) out = new LiteralText(pre);
				else out.append(new LiteralText(pre));
			}
			else
			{
				if (out == null) out = new LiteralText(pre);
				else out.append(new LiteralText(pre));
				
				Style style = MinecraftStyle.NoStyle
								.withInsertion(matcher.group(1))
								.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(matcher.group(1))));
				
				out.append(new LiteralText(CodePoints.Utf16ToString(foundEmote)).setStyle(style));
			}
			
			lastIndex = matcher.end();
		}
		if (lastIndex < textPart.length())
		{
			if (out == null) out = new LiteralText(textPart.substring(lastIndex));
			else out.append(new LiteralText(textPart.substring(lastIndex)));
		}
		
		// Just in case
		if (out == null) out = new LiteralText("");
		
		for (Text sibling : oldSiblings)
		{
			if (sibling instanceof MutableText) out.append(TransformTextRec((MutableText)sibling));
			else out.append(TransformTextRec(new LiteralText(sibling.getString())));
		}
		
		return out;
	}
	
	private TranslatableText TransformText(TranslatableText text)
	{
		for (int i = 1; i < text.getArgs().length; i++)
		{
			if (text.getArgs()[i] instanceof String) text.getArgs()[i] = TransformTextRec(new LiteralText((String)text.getArgs()[1]));
			else if (text.getArgs()[i] instanceof MutableText) text.getArgs()[i] = TransformTextRec((MutableText)text.getArgs()[1]);
		}
		
		return text;
	}
}