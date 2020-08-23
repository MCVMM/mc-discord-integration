package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.CodeBlockNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeBlockRule<F, S extends IParserState<S>> extends Rule<F, CodeBlockNode<F>, S>
{
	private static final Pattern Regex = Pattern.compile("^```(([A-z0-9_+\\-.]+?)\n)?\n*([^\n].*?)\n*```", Pattern.DOTALL);
	
	public CodeBlockRule()
	{
		super(Regex);
	}
	
	@NotNull
	@Override
	public ParseSpec<F, CodeBlockNode<F>, S> parse(@NotNull Matcher matcher, @NotNull Parser<F, ? super CodeBlockNode<F>, S> parser, S state)
	{
		String language = matcher.group(2);
		String code = matcher.group(3);
		if (code == null) code = "";
		
		return ParseSpec.createTerminal(new CodeBlockNode<>(language, code), state);
	}
}