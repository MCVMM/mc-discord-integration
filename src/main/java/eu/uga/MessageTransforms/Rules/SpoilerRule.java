package eu.uga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uga.MessageTransforms.IParserState;
import eu.uga.MessageTransforms.Nodes.SpoilerNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpoilerRule<F, S extends IParserState<S>> extends Rule<F, SpoilerNode<F>, S>
{
	private static final Pattern Regex = Pattern.compile("^\\|\\|([\\s\\S]+?)\\|\\|");
	
	public SpoilerRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<F, SpoilerNode<F>, S> parse(@NotNull Matcher matcher, @NotNull Parser<F, ? super SpoilerNode<F>, S> parser, S state)
	{
		return ParseSpec.createNonterminal(new SpoilerNode<>(), state, matcher.start(1), matcher.end(1));
	}
}
