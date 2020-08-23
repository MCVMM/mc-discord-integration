package eu.uniga.MessageTransforms.Rules;

import com.discord.core.parser.ParseSpec;
import com.discord.core.parser.Parser;
import com.discord.core.parser.Rule;
import eu.uniga.MessageTransforms.IParserState;
import eu.uniga.MessageTransforms.Nodes.BlockQuoteNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockQuoteRule<F, S extends IParserState<S>> extends Rule.BlockRule<F, BlockQuoteNode<F>, S>
{
	private static final Pattern BlockQuotePattern = Pattern.compile("^(?: *>>> +(.*)| *>(?!>>) +([^\\n]*\\n?))", Pattern.DOTALL);
	
	public BlockQuoteRule()
	{
		super(BlockQuotePattern);
	}
	
	@Nullable
	@Override
	public Matcher match(@NotNull CharSequence inspectionSource, @Nullable String lastCapture, S state)
	{
		// Only do this if we aren't already in a quote.
		if (state.IsInQuote()) return null;
		return super.match(inspectionSource, lastCapture, state);
	}
	
	@NotNull
	@Override
	public ParseSpec<F, BlockQuoteNode<F>, S> parse(@NotNull Matcher matcher, @NotNull Parser<F, ? super BlockQuoteNode<F>, S> parser, S state)
	{
		int groupIndex = matcher.group(1) != null ? 1 : 2;
		S newState = state.NewBlockQuoteState(true);
		return ParseSpec.createNonterminal(new BlockQuoteNode<F>(), newState, matcher.start(groupIndex), matcher.end(groupIndex));
	}
}
