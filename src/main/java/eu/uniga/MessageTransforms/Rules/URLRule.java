package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.URLNode;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLRule<F, S extends IParserState<S>> extends Rule<F, URLNode<F>, S>
{
	private static final Pattern Regex = Pattern.compile("^(https?://[^\\s<]+[^<.,:;\"')\\]\\s])");
	private static final Pattern RegexNoEmbed = Pattern.compile("^<(https?://[^\\s<]+[^<.,:;\"')\\]\\s])>");
	
	private final boolean _embed;
	
	public URLRule(boolean embed)
	{
		super(embed ? Regex : RegexNoEmbed);
		
		_embed = embed;
	}
	
	@NotNull
	@Override
	public ParseSpec<F, URLNode<F>, S> parse(@NotNull Matcher matcher, @NotNull Parser<F, ? super URLNode<F>, S> parser, S state)
	{
		// We do not care about embed in Minecraft, always URLNode
		return ParseSpec.createTerminal(new URLNode<>(matcher.group(1)), state);
	}
	
	protected boolean IsEmbed()
	{
		return _embed;
	}
}
