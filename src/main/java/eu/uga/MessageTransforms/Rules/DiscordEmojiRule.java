package eu.uga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uga.MessageTransforms.FormattingContext;
import eu.uga.MessageTransforms.IParserState;
import eu.uga.MessageTransforms.Nodes.EmojiNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordEmojiRule<S extends IParserState<S>> extends Rule<FormattingContext, EmojiNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^<(a)?:([a-zA-Z_0-9]+):(\\d+)>");
	
	public DiscordEmojiRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, EmojiNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super EmojiNode<FormattingContext>, S> parser, S state)
	{
		boolean animated = true;
		
		if (matcher.group(1) == null) animated = false;
		else if (matcher.group(1).isEmpty()) animated = false;
		
		String name = matcher.group(2);
		long snowflake = 0;
		
		try
		{
			snowflake = Long.parseUnsignedLong(matcher.group(3));
		}
		catch (Exception ignored) { }
		
		return ParseSpec.createTerminal(EmojiNode.FromDiscordName(animated, name, snowflake), state);
	}
}
