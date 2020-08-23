package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.MultipleMentionNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleMentionRule<F, S extends IParserState<S>> extends Rule<FormattingContext, MultipleMentionNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^@(everyone|here)");
	
	public MultipleMentionRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, MultipleMentionNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super MultipleMentionNode<FormattingContext>, S> parser, S state)
	{
		return ParseSpec.createTerminal(new MultipleMentionNode<>(matcher.group(1)), state);
	}
}
