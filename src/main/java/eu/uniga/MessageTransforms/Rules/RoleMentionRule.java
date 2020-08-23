package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.RoleMentionNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoleMentionRule<F, S extends IParserState<S>> extends Rule<FormattingContext, RoleMentionNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^<@&(\\d+)>");
	
	public RoleMentionRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, RoleMentionNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super RoleMentionNode<FormattingContext>, S> parser, S state)
	{
		long id = 0;
		
		try
		{
			id = Long.parseUnsignedLong(matcher.group(1));
		}
		catch (Exception ignored) { }
		
		return ParseSpec.createTerminal(new RoleMentionNode<>(id), state);
	}
}
