package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.InlineCodeBlockNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlineCodeBlockRule<F, S extends IParserState<S>> extends Rule<F, InlineCodeBlockNode<F>, S>
{
	private static final Pattern Regex = Pattern.compile("^(`+)\\s*([\\s\\S]*?[^`])\\s*\\1(?!`)", Pattern.DOTALL);
	
	public InlineCodeBlockRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<F, InlineCodeBlockNode<F>, S> parse(@NotNull Matcher matcher, @NotNull Parser<F, ? super InlineCodeBlockNode<F>, S> parser, S state)
	{
		String code = matcher.group(2);
		
		return ParseSpec.createTerminal(new InlineCodeBlockNode<>(code), state);
	}
}
