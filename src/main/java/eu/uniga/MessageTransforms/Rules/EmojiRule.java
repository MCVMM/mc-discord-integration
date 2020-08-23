package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.EmojiNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiRule<S extends IParserState<S>> extends Rule<FormattingContext, EmojiNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^:([^\\s:]+?(?:::skin-tone-\\d)?):");
	
	public EmojiRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, EmojiNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super EmojiNode<FormattingContext>, S> parser, S state)
	{
		String shortName = matcher.group(1);
		if (shortName == null) shortName = matcher.group(2);
		
		return ParseSpec.createTerminal(EmojiNode.FromShortName(null, shortName), state);
	}
}
