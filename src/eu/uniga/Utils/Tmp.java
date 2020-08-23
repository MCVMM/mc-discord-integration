package eu.uniga.Utils;


import core.markdown.AdvancedMarkdownRules;
import core.markdown.SimpleMarkdownRules;
import core.node.Node;
import core.parser.Parser;
import eu.uniga.Utils.Tmp.ParseState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Tmp
{
	public static void main(String[] args)
	{
		String text = "Some text\n" +
						"Some other text\n" +
						"\n" +
						"**Bold text.**\n" +
						"> quoted text\n" +
						"*~~bold strikethrough~~*";
		
		System.out.println("---test---");
		
		Parser<Node, ParseState> parser = new Parser<Node, ParseState>()
						.addRule(AdvancedMarkdownRules.createBlockQuoteRule())
						.addRules(SimpleMarkdownRules.createSimpleMarkdownRules());
		
		
		List<Node> output = parser.parse(text, new ParseState(false));
		
		return;
	}
	
	static class ParseState implements AdvancedMarkdownRules.BlockQuoteState<ParseState> {
		private final boolean _isInQuote;
		
		public ParseState(boolean isInQuote)
		{
			_isInQuote = isInQuote;
		}
		
		@NotNull
		@Override
		public ParseState newBlockQuoteState(boolean isInQuote) {
			return new ParseState(isInQuote);
		}
		
		@Override
		public boolean isInQuote() {
			return _isInQuote;
		}
	}
}
