package eu.uniga.MessageTransforms.Rules;

import com.discord.core.node.TextNode;
import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.FormattingContext;
import eu.uniga.MessageTransforms.IParserState;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscapeEscapeRule<S extends IParserState<S>> extends Rule<FormattingContext, TextNode<FormattingContext>, S>
{
	private static final Pattern Regex = Pattern.compile("^(¯\\\\_\\(ツ\\)_/¯)");
	
	public EscapeEscapeRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<FormattingContext, TextNode<FormattingContext>, S> parse(@NotNull Matcher matcher, @NotNull Parser<FormattingContext, ? super TextNode<FormattingContext>, S> parser, S state)
	{
		return ParseSpec.createTerminal(new TextNode<>("¯\\_(ツ)_/¯"), state);
	}
}
