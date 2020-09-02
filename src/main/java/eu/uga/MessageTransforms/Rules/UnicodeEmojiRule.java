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

public class UnicodeEmojiRule<S extends IParserState<S>> extends Rule<FormattingContext, EmojiNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^([\\x{20a0}-\\x{32ff}]|[\\x{1f000}-\\x{1ffff}]|[\\x{fe4e5}-\\x{fe4ee}])");
	
	public UnicodeEmojiRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, EmojiNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super EmojiNode<FormattingContext>, S> parser, S state)
	{
		String codepoint = matcher.group(1);
		
		return ParseSpec.createTerminal(EmojiNode.FromUnicode(codepoint), state);
	}
}
