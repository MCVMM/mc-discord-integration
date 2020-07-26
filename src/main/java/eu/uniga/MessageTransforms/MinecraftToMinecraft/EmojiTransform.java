package eu.uniga.MessageTransforms.MinecraftToMinecraft;

import eu.uniga.MessageTransforms.IMessageTransform;
import eu.uniga.MessageTransforms.SurrogatePairsDictionary;
import eu.uniga.Utils.Codepoints;
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
	
	private TranslatableText TransformText(TranslatableText text)
	{
		if (text.getArgs()[1] instanceof String) text.getArgs()[1] = TransformTextRec(new LiteralText((String)text.getArgs()[1]));
		else if (text.getArgs()[1] instanceof MutableText) text.getArgs()[1] = TransformTextRec((MutableText)text.getArgs()[1]);
		
		return text;
	}
	
	private MutableText TransformTextRec(MutableText text)
	{
		List<Text> oldSiblings = new ArrayList<>(text.getSiblings().size());
		oldSiblings.addAll(text.getSiblings());
		text.getSiblings().clear();
		
		LiteralText out;
		
		int lastIndex;
		String textPart = text.getString();
		
		Matcher matcher = _pattern.matcher(textPart);
		StringBuilder buffer = new StringBuilder();
		
		if (matcher.find())
		{
			out = new LiteralText(textPart.substring(0, matcher.start()));
			
			lastIndex = FoundMatch(out, matcher);
		}
		else
		{
			out = new LiteralText(textPart);
			lastIndex = Integer.MAX_VALUE;
		}
		
		while (matcher.find())
		{
			lastIndex = FoundMatch(out, matcher);
		}
		if (lastIndex < textPart.length())
		{
			out.append(new LiteralText(textPart.substring(lastIndex)));
		}
		
		oldSiblings.forEach(sibling ->
		{
			if (sibling instanceof MutableText) out.append(TransformTextRec((MutableText)sibling));
			else out.append(TransformTextRec(new LiteralText(sibling.getString())));
		});
		
		return out;
	}
	
	private int FoundMatch(LiteralText out, Matcher matcher)
	{
		int lastIndex;
		Integer found = _emojiDictionary.GetSurrogatePairFromShortName(matcher.group(1));
		String append;
		
		if (found == null) append = matcher.group(1);
		else append = Codepoints.Utf16ToString(found);
		
		out.append(new LiteralText(append).setStyle(MinecraftStyle.NoStyle));
		
		lastIndex = matcher.end();
		
		return lastIndex;
	}
}